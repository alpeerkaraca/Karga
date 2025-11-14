# Karga - Kubernetes (k8s) Manifestleri

Bu klasör, "Karga" uygulamasının tüm bağımlılıkları (MySQL, Redis, Kafka) ve uygulamanın kendisi (`karga-app`) için gerekli Kubernetes manifestlerini içerir.

## Dosya Yapısı

* **Manifestler (Git'e Dahil Edilenler):**
    * `karga-configmap.yaml`: "Karga" (Spring Boot) için tüm ortam değişkenleri (veritabanı host'ları, Kafka adresleri vb.).
    * `karga-deployment.yaml`: "Karga" (Spring Boot) pod'unu tanımlar.
    * `karga-service.yaml`: "Karga"yı dış dünyaya (NodePort) açan servis.
    * `karga-mysql-cluster.yaml`: MySQL Operatör'ünden bir veritabanı cluster'ı istemek için kullanılır.
    * `karga-kafka.yaml`: Bitnami (legacy) Kafka cluster'ının tam tanımı (SSL Kullanır).
    * `karga-secret.example.yaml`: **ŞABLON.** `karga-secret.yaml` dosyasını oluşturmak için kullanılır.
    * `README.md`: Bu dosya.
    * `README-SSL.md`: Tüm `.key`, `.crt`, `.jks` dosyalarını sıfırdan oluşturma talimatları.
    * `openssl.cnf`: MySQL sertifikalarını oluşturmak için `openssl` konfigürasyonu.

* **Üretilen Dosyalar (Git'e Dahil Edilmeyenler):**
    * `karga-secret.yaml`: **GİZLİ.** `karga-secret.example.yaml`'den oluşturulur. Tüm şifreleri içerir.
    * `*.key`, `*.crt`, `.srl`, `.jks` vb.: **GİZLİ.** SSL sertifikaları. `README-SSL.md`'ye bakarak yeniden üretilmelidir.

## Yeni Ortam Kurulumu (Adım Adım)

1.  **Ön Koşul:** `kubectl`'in lokal cluster'a (Minikube/Podman Desktop) bağlı olduğundan emin ol. `dev` namespace'inin var olduğundan emin ol.

2.  **Operatörleri Kur:**
    * **MySQL Operatör:** `helm repo add mysql-operator https://mysql.github.io/mysql-operator/`
    * `helm install mysql-operator mysql-operator/mysql-operator --namespace mysql-operator --create-namespace`
    * **Redis:** (Bu, Operatör gerektirmez) `helm repo add bitnami https://charts.bitnami.com/bitnami`

3.  **Hassas Dosyaları Oluştur (Sertifikalar):**
    * Tüm sertifikaları ve anahtarları yeniden oluşturmak için `README-SSL.md` dosyasındaki talimatları izle.
    * Bu adımlar `karga-db-cluster-ca` ve `karga-db-cluster-tls` Secret'lerini oluşturacaktır.

4.  **Hassas Dosyaları Oluştur (Şifreler):**
    * `karga-secret.example.yaml` dosyasını `karga-secret.yaml` olarak kopyala.
    * İçindeki `...` olan yerleri, belirlediğin MySQL şifresi, Kafka şifresi ve TrustStore şifresinin **Base64** halleriyle doldur.
    * `kubectl apply -f karga-secret.yaml -n dev`

5.  **Bağımlılıkları Ayağa Kaldır:**
    * `kubectl apply -f karga-mysql-cluster.yaml -n dev` (MySQL'i başlatır)
    * `kubectl apply -f karga-kafka.yaml -n dev` (Kafka'yı başlatır)
    * `helm install karga-redis bitnami/redis -n dev` (Redis'i başlatır)

6.  **"Karga" Uygulamasını Başlat:**
    * "Karga" Docker imajının (`karga-app:latest`) `src/main/resources/kafka.client.truststore.jks` dosyasını içerdiğinden ve lokal cluster'a (`minikube image load`) yüklendiğinden emin ol.
    * `kubectl apply -f karga-configmap.yaml -n dev`
    * `kubectl apply -f karga-deployment.yaml -n dev`
    * `kubectl apply -f karga-service.yaml -n dev`

7.  **Erişim:**
    * `minikube service karga-app-service -n dev`