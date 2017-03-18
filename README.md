# Sayı Tahmin Oyunu için Analiz ve Çözüm Yardım Programları
|English Summary|
|---------------|
|This project is about the **Bulls and Cows** game (see the [wikipedia article](https://en.wikipedia.org/wiki/Bulls_and_Cows) or [a sample web game](http://www.mathsisfun.com/games/bulls-and-cows.html). It is similar to **Mastermind** but not the same.). It is a game where you try to find out a (usually) 4 digit number by making guesses. Again usually, the digits are not repeated, and the number may start with 0. You make a 4 digit guess, and the game tells you how many of your digits are correct but in the wrong place (cows), and how many are both correct and in the right position (bulls).
Since there are many resources on the game in English, ranging from online games to academic papers on the analysis of the game, I decided to do this project (code and documentation) all in Turkish (my native language). If there is any interest, I can give English explanations as well.|

Bu repo'da Sayı Tahmin Oyunu'nun analizi ve çözümü üzerine geliştirilmiş bazı kodlar bulacaksınız. 

## Sayı Tahmin Oyunu
Bilgisayar (veya birinci oyuncu) 4 basamaklı bir sayı tutar. Sayı 0 ile başlayabilir ve rakamlar tekrar etmez. Oyuncu 4 basamaklı bir sayı söyleyerek tahminde bulunur. Bilgisayar, kaç rakamı doğru bildiğinizi şu şekilde söyler: kaç tane *rakam doğru ama yeri yanlış* (buna B diyelim), kaç tane *rakam ve yeri doğru* (A diyelim). Mesela:
> Sayı: **1234**
> Tahmin: **3524**
> Sonuç: 1A 2B *(4 -> 1A. 3 ve 2 -> 2B)*

## Çözüm Yardım Programı (Java)
Bu program, sayı tahmin oyunu oynarken size yardımcı olacaktır. Her tahmin ve sonuç ardından, olabilecek sayı listesini sunar. İlk başta 5040 alternatif vardır. Tahminde bulunup 1A2B vb. cevaplar aldıkça alternatifler azalır. 

Ayrıca tahmin aşamasında, yapabileceğiniz farklı tahminleri ve bunların olabilecek sayılarda nasıl sonuç vereceğini size sunar (ne demek istediğimi ileride açmaya çalışacağım). Olası tahminleri belli matematiksel kriterlere göre (entropi, max, ortalama vb.) sıralayabilirsiniz.

Matematiksel olarak önerilen tahminleri takip ederek sayı tahmin oyununu en fazle 6 adımda çözebiliyorsunuz.

Program JavaFX arayüz sistemi kullanılarak yazılmıştır.

## Oyun Ağacının Analizine Yönelik Python Kod ve Çıktılar (py, txtout klasörleri)
Java GUI programın işlevini gören ama araştırma amaçlı, komut satırı python kodlarını içerir. Oyun oynama, oyun ağacını çıkarma, farklı matematiksel stratejilerin ortalama ve en uzun oyun uzunluğunu hesaplama vb. işlemler için fonksiyonlar bulunmaktadır.

**Bu kısa açıklamaları zaman içinde genişletmeye çalışacağım.**
