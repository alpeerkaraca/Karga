# Karga - SSL/TLS Sertifika Oluşturma Kılavuzu

Bu belge, `k8s` klasöründe `.gitignore` tarafından engellenen tüm hassas sertifika (`.crt`), anahtar (`.key`) ve güven deposu (`.jks`) dosyalarının nasıl yeniden oluşturulacağını açıklar.

**Ön Koşullar:**
* `openssl` komut satırı aracı (Genellikle Git Bash ile birlikte gelir).
* `keytool` komut satırı aracı (Java JDK'nın `bin` klasöründe bulunur).
* `kubectl` komut satırı aracı.

Tüm komutlar bu `k8s` klasörü içinden çalıştırılmalıdır.

---

## 1. MySQL Operatör Sertifikaları (`openssl`)

Bu adımlar, MySQL Operatör'ünün "hayır, ben kendi sertifikamı yapmayacağım" (`tlsUseSelfSigned: false` durumu) dediği ve bizden özel sertifika beklediği senaryo içindir.

### Adım 1.1: `openssl.cnf` Dosyası

Tüm `openssl` komutları, sertifikalara doğru sunucu adlarını (SAN - Subject Alternative Names) eklemek için aşağıdaki `openssl.cnf` dosyasına ihtiyaç duyar. Bu dosya Git'e dahil edilmiştir.

```
[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
prompt = no

[req_distinguished_name]
# CN (Common Name)
CN = karga-db-cluster-router.dev.svc

[v3_req]
subjectAltName = @alt_names

[alt_names]
# MySQL Operatörünün aradığı tüm DNS isimleri
DNS.1 = karga-db-cluster-router
DNS.2 = karga-db-cluster-router.dev
DNS.3 = karga-db-cluster-router.dev.svc
DNS.4 = karga-db-cluster-router.dev.svc.cluster.local
DNS.5 = *.karga-db-cluster-instances.dev.svc.cluster.local
DNS.6 = localhost
```

### Adım 1.2: Kendi Sertifika Otoriteni (CA) Oluştur

Bu, diğer tüm sertifikaları imzalayacak olan "güven kökü"dür.

```
# 1. CA için özel anahtar oluştur (Dosya: karga-ca.key)
openssl genrsa -out karga-ca.key 4096

# 2. CA için genel, kendi kendine imzalı sertifika oluştur (Dosya: karga-ca.crt)
openssl req -x509 -new -nodes -key karga-ca.key -sha256 -days 3650 -out karga-ca.crt -subj "/CN=KargaInternalCA"
```

### Adım 1.3: MySQL Sunucu Sertifikasını Oluştur ve İmzala

Şimdi, `karga-ca.key`'i kullanarak MySQL sunucusu için bir sertifika imzalayacağız.

```
# 1. Sunucu için özel anahtar oluştur (Dosya: karga-server.key)
openssl genrsa -out karga-server.key 4096

# 2. Sunucu için bir Sertifika İmza İsteği (CSR) oluştur (Dosya: karga-server.csr)
# (Ayarları openssl.cnf dosyasından alacak)
openssl req -new -key karga-server.key -out karga-server.csr -config openssl.cnf

# 3. CSR'yi kendi CA'mızla imzala (Dosyalar: karga-server.crt ve karga-ca.srl)
openssl x509 -req -in karga-server.csr -CA karga-ca.crt -CAkey karga-ca.key \
-CAcreateserial -out karga-server.crt -days 365 -sha256 \
-extfile openssl.cnf -extensions v3_req
```

### Adım 1.4: Kubernetes Secret'lerini Oluştur

Oluşturduğumuz dosyaları (`karga-ca.crt`, `karga-server.crt`, `karga-server.key`) Operatör'ün okuyabilmesi için `Secret`'lara dönüştür:

```
# 1. CA Sertifikasını Secret'a yükle
kubectl create secret generic karga-db-cluster-ca \
  --from-file=ca.crt=karga-ca.crt \
  -n dev

# 2. Sunucu Sertifikasını ve Anahtarını 'tls' Secret'ına yükle
kubectl create secret tls karga-db-cluster-tls \
  --cert=karga-server.crt \
  --key=karga-server.key \
  -n dev
```

---

## 2. Kafka İstemci Güven Deposu (`keytool`)

Kafka (Bitnami) sertifikaları farklı çalışır. `helm template` komutu, Kafka için CA'yı ve sertifikaları *otomatik olarak* oluşturur ve bunları `karga-kafka-tls` adında bir `Secret` içine koyar.

Bizim `karga-app`'in bu CA'ya güvenmesi için o `Secret`'tan CA'yı çekip bir `truststore.jks` dosyası oluşturmamız gerekir.

### Adım 2.1: Kafka CA Sertifikasını Cluster'dan Çek

```
# Bitnami'nin oluşturduğu 'karga-kafka-tls' Secret'ından 'ca.crt'yi çek
# ve 'karga-kafka-ca.crt' adıyla kaydet.
kubectl get secret karga-kafka-tls -n dev -o jsonpath='{.data.ca\.crt}' | base64 -d > karga-kafka-ca.crt
```

### Adım 2.2: Java TrustStore (`.jks`) Oluştur

```
# 'karga-kafka-ca.crt' dosyasını kullanarak 'kafka.client.truststore.jks' oluştur
keytool -import -trustcacerts -alias karga-kafka-ca -file karga-kafka-ca.crt -keystore kafka.client.truststore.jks
```

Bu komut sana iki şey soracak:

1.  **`Enter keystore password:`** (Yeni bir şifre belirle, örn: `kargasifre123`).
2.  **`Trust this certificate? [no]:`** `yes` (veya `e`) yazıp onayla.

### Adım 2.3: TrustStore'u Uygulamaya Kopyala

Oluşturduğun bu `kafka.client.truststore.jks` dosyasını, "Karga" ana proje klasöründeki `src/main/resources/` dizinine kopyalaman gerekir.

`Dockerfile`'ımız bu dosyayı oradan alıp `/app/kafka.client.truststore.jks` yoluna kopyalamak ve `karga-configmap.yaml`'in okumasını sağlamak için ayarlanmıştır.