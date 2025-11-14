# 🚕 Karga - Ride-Sharing Backend Projesi

[![Java](https://img.shields.io/badge/Java-21-blue.svg?logo=openjdk&style=for-the-badge)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.5-brightgreen.svg?logo=spring&style=for-the-badge)](https://spring.io/projects/spring-boot)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-darkblue.svg?logo=kubernetes&style=for-the-badge)](https://kubernetes.io)
[![Kafka](https://img.shields.io/badge/Apache_Kafka-black.svg?logo=apachekafka&style=for-the-badge)](https://kafka.apache.org)

"Karga", modern bir araç paylaşım (ride-sharing) uygulaması için geliştirilmiş, event-driven (olay güdümlü) bir backend sistemidir.

Bu proje, Spring Boot 3, Java 21, Spring Data (JPA/Redis) ve Spring Kafka kullanarak Domain-Driven Design (DDD) prensiplerine uygun olarak tasarlanmıştır. Tüm altyapı, Kubernetes üzerinde çalışacak şekilde yapılandırılmıştır.

## 🚀 Teknoloji Mimarisi

Bu proje, birbiriyle konuşan ve her biri kendi sorumluluğunu taşıyan servislerden oluşur.

* **Uygulama:** Java 21, Spring Boot 3.5.5
* **Veritabanı:** MySQL 8.0 (Operatör ile kuruldu)
* **Cache (Önbellek):** Redis
* **Mesajlaşma (Messaging):** Apache Kafka (Bitnami Legacy Chart ile, KRaft modunda)
* **Güvenlik:** Spring Security (JWT ile Token tabanlı kimlik doğrulama)
* **Orkestrasyon:** Kubernetes (Minikube & Podman ile test edildi)
* **Paketleme:** Helm (Sadece `template` için) & `kubectl`
* **Container:** Podman (ve Dockerfile)



## 🏗️ Proje Yapısı

Proje, Domain-Driven Design (DDD) ilhamlı bir yapı kullanır:

* **`src/main/java`**: Ana Spring Boot uygulama kodu.
    * `/user`: Kullanıcı kaydı, login (JWT) ve profil yönetimi.
    * `/driver`: Sürücü durumu (ONLINE/OFFLINE), lokasyon güncellemeleri (Kafka üzerinden).
    * `/trip`: Yolculuk talebi, sürücü bulma ve yolculuk durum yönetimi.
    * `/payment`: Stripe entegrasyonu ve ödeme işlemleri (Kafka üzerinden).
* **`src/main/resources`**: Spring Boot konfigürasyon dosyaları (`application.properties`) ve Kafka için gereken `kafka.client.truststore.jks` dosyası.
* **`Dockerfile`**: Uygulamayı `eclipse-temurin:21-jre-ubi10-minimal` (minimal JRE) imajı ile paketler. `truststore` dosyasını `/app/` dizinine kopyalamak için özel olarak ayarlanmıştır.
* **`k8s/`**: Tüm Kubernetes kurulum manifestlerini, SSL sertifika oluşturma talimatlarını (`README-SSL.md`) ve şablonları (`karga-secret.example.yaml`) içerir.

## 🏁 Başlarken: Projeyi Kubernetes'te Çalıştırma

Bu proje, lokalde değil, doğrudan Kubernetes üzerinde çalışmak için tasarlanmıştır.

### Ön Koşullar

1.  Java 21 & Maven (Uygulamayı build etmek için)
2.  `podman` (veya `docker`) (İmajı build etmek için)
3.  `minikube` (veya benzeri bir lokal Kubernetes cluster'ı)
4.  `kubectl`
5.  `helm` (Sadece `template` komutu için)
6.  `openssl` (MySQL sertifikaları için)
7.  `keytool` (Kafka `truststore`'u için)

### Kurulum (Adım Adım)

Tüm manifestler ve detaylı talimatlar `k8s/` klasöründedir.

1.  **`k8s` Klasörüne Git:**
    ```powershell
    cd k8s
    ```

2.  **`k8s/README.md` Dosyasını Oku:**
    Bu proje, Git'e gönderilmeyen birçok hassas (`.key`, `.crt`, `.jks`, `karga-secret.yaml`) dosyaya bağlıdır.

    `k8s/README.md` ve `k8s/README-SSL.md` dosyalarındaki talimatları izleyerek:
    1.  Gerekli Operatörleri (MySQL) ve Helm depolarını (Redis) kur.
    2.  Tüm MySQL SSL sertifikalarını (`openssl` ile) oluştur ve `Secret` olarak cluster'a yükle.
    3.  Kafka `truststore.jks` dosyasını (`keytool` ile) oluştur.

3.  **Hassas Şifreleri Ayarla:**
    * `karga-secret.example.yaml` dosyasını `karga-secret.yaml` olarak kopyala.
    * İçini MySQL root şifren, Kafka `user1` şifresi ve `truststore` şifrenin **Base64** halleriyle doldur.
    * `kubectl apply -f karga-secret.yaml -n dev`

4.  **Altyapıyı Başlat:**
    * `kubectl apply -f karga-mysql-cluster.yaml -n dev` (MySQL'i başlatır)
    * `kubectl apply -f karga-kafka.yaml -n dev` (Kafka'yı başlatır)
    * `helm install karga-redis bitnami/redis -n dev` (Redis'i başlatır)

5.  **"Karga" Docker İmajını Hazırla:**
    * `k8s/` klasöründe `keytool` ile oluşturduğun `kafka.client.truststore.jks` dosyasını, `src/main/resources/` klasörüne kopyala.
    * Projenin ana dizinine (`Dockerfile`'ın olduğu yer) git.
    * İmajı `podman` ile build et:
        ```powershell
        podman build -t karga-app:latest .
        ```
    * Yeni imajı Minikube cluster'ına yükle:
        ```powershell
        minikube image load karga-app:latest
        ```

6.  **"Karga" Uygulamasını Başlat:**
    * `k8s` klasörüne geri dön.
    * `kubectl apply -f karga-configmap.yaml -n dev`
    * `kubectl apply -f karga-deployment.yaml -n dev`
    * `kubectl apply -f karga-service.yaml -n dev`

7.  **Uygulamaya Eriş:**
    Servisi host makinana (Windows) açmak için bu komutu çalıştır. Tarayıcın otomatik olarak açılacaktır:
    ```powershell
    minikube service karga-app-service -n dev