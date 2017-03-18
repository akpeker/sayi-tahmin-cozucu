/**
 * Sayý Tahmin Yardým Aracý GUI'sinde, Analiz tablosunda gösterilen tahmin histogramý verilerini hesaplayan/saklayan sýnýf.
 * 
 * Histogram sýnýfa dýþarýdan verilir. Entropi, max, ortalama, uzunluk vb. bu sýnýfta hesaplanýr.
 * 
 * Ayný zamanda histogram eþitliði kontrolü için metot sunar.
 * Eþdeðer tahminler listesi bulundurur.
 */

package sayitahmin;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TahminHist {
    private final SimpleStringProperty tahmin,histStr;
    private final SimpleDoubleProperty entropi,ortalama;
    private final SimpleIntegerProperty max, uzunluk;
    private final int[] hist;
    public List<String> eþdeðerler;
    private SimpleIntegerProperty eþdeðerSayýsý;
    
	public TahminHist(String tahmin, int[] hist) {
		this.tahmin = new SimpleStringProperty(tahmin);
		this.hist = hist;
		
		int cnt = 0, max = 0, top = 0;
		for (int x : hist) {
			if (x > 0) {
				cnt++;
				top += x;
				if (x > max) max = x;
			}
		}
		this.max = new SimpleIntegerProperty(max);
		this.uzunluk = new SimpleIntegerProperty(cnt);
		this.ortalama = new SimpleDoubleProperty(1.0*top/cnt);
		this.entropi = new SimpleDoubleProperty( entropiHesapla(top) );
		this.histStr = new SimpleStringProperty( hist2str() );
		
		eþdeðerler = new ArrayList<String>();
		eþdeðerler.add(tahmin);		// Tahminin kendisini de eþdeðer kümesinde dahil edelim.
		eþdeðerSayýsý = new SimpleIntegerProperty(eþdeðerler.size());
	}
	
	/**
	 * Verilen hist2 histogramýnýn, bu nesnenin histogramýna eþit olup olmadýðý boolean olarak döndürülür.
	 * Bu nesnenin histogramý uzunluk olarak temel alýnýr. hist2 daha kýsa ise false döndürür. Daha uzunsa, fazlalýk kýsmý dikkate alýnmaz.
	 * Uzunluk eþitlik þartý aramama sebebi, uzun ama 0 dolu histogramlar için kapýyý bir derece açýk býrakmak.
	 * 
	 * @param hist2		Eþdeðerliði karþýlatýrýlacak histogram (int dizisi). En az bu nesnenin histogramý kadar uzun olmalý.
	 * @return			Histogramlar  eþitse true, deðilse false döndürür.
	 */
	public boolean eþdeðerMi(int[] hist2) {
		if (hist2.length < hist.length) return false;
		for (int i=0; i<hist.length; i++) {
			if (hist[i] != hist2[i]) return false;
		}
		return true;
	}

	/**
	 * Verilen th2 TahminHist nesnesinin, bu nesnenin histogramýna eþit olup olmadýðý boolean olarak döndürülür.
	 * Bu nesnenin histogramý uzunluk olarak temel alýnýr. th2'nin histogramý daha kýsa ise false döndürür. Daha uzunsa, fazlalýk kýsmý dikkate alýnmaz.
	 * Uzunluk eþitlik þartý aramama sebebi, uzun ama 0 dolu histogramlar için kapýyý bir derece açýk býrakmak.
	 * 
	 * @param th2		Eþdeðerliði karþýlatýrýlacak histogram (TahminHist). En az bu nesnenin histogramý kadar uzun olmalý.
	 * @return			Histogramlar  eþitse true, deðilse false döndürür.
	 */
	public boolean eþdeðerMi(TahminHist th2) {
		return eþdeðerMi(th2.hist);
	}

	/**
	 * Verilen tahmin'i, bu tahmin'in eþdeðerleri listesine ekler, eþdeðerSayýsý'ný 1 artýrýr.
	 * Tahmin2'nin eþdeðer olup olmadýðý önceden, eþdeðerMi(int[] hist2) metoduyla kontrol edilmelidir.
	 * 
	 * @param tahmin2	Eþdeðer listesine eklenecek tahmin (String)
	 */
	public void eþdeðerEkle(String tahmin2) {
		eþdeðerler.add(tahmin2);
		eþdeðerSayýsý.set(eþdeðerler.size());
		//System.out.println("Eþdeðer: " + tahmin + " == " + tahmin2 + " (" + eþdeðerSayýsý.get() + ")");
	}
	
	/**
	 * Bu histogramýn entopisini hesaplar: 
	 * 		Entropi = -toplam(pi*log(pi)), 
	 * 		pi = i'inci olasýlýk deðeri.	pi = hist[i]/toplam(hist)
	 * 
	 * Log taban 2 kullanýlýr. Bu þekilde hesaplanan entropi birimi bit olarak geçer (örn: ... bit'lik bilgi).
	 * 
	 * Entropi, bu olasýlýk daðýlýmýnýn ne kadar belirsizlik içerdiðinin bir göstergesidir. 
	 * Belirsizlik ne kadar yüksekse, bu deneyin yapýlmasý da o kadar çok bilgi kazandýrýr.
	 * Örneðin yazý/tura deneyinde yazý gelme ihtimali 1 ise, o yazý/tura deneyi bize bir bilgi kazandýrmaz; sonucu zaten biliyoruz.
	 * Yazý/tura atmada en yüksek bilgi kazanýmý, eðer olasýlýklar 0.5/0.5 eþit ise olur; bu durumda belirsizlik en yüksektir ve parayý atmak bize en çok bilgiyi kazandýrýr.
	 * 
	 * Histogram toplamý daha önce hesaplandýðý için bu metoda argüman olarak gönderilmesi tercih edildi.
	 *  
	 * @param top	Histogram elemanlarýnýn toplamý
	 * @return		Entropi (2'lik tabana göre)
	 */
	private double entropiHesapla(int top) {
		double e = 0;
		double log2 = Math.log(2); // ln'den log2'ye çevirmek için
		for (int x : this.hist) {
			if (x > 0) {
				double p = (double) x / top;
				e -= p*Math.log(p)/log2;
			}
		}
		return e;
	}
	
	/**
	 * Bu histogramý, çýktýya uygun bir String olarak döndürür.
	 * 
	 * @return		00:123,01:72,02:35,... þeklinde String. 0 olan elemanlar dahil edilmez.
	 */
	private String hist2str() {
		String s = "";
		for (int i=0; i<this.hist.length; i++) {
			if (hist[i] > 0) {
				int A = i/5;
				int B = i%5;
				s += "["+A+B+":"+hist[i]+"] ";
			}
		}
		return s;
	}

    public String getTahmin() {return tahmin.get();}
    public void setTahmin(String val) {tahmin.set(val);}
    public String getHistStr() {return histStr.get();}
    public void setHistStr(String val) {histStr.set(val);}
    public double getEntropi() {return Math.round(1000*entropi.get())/1000.0;}
    public void setEntropi(double val) {entropi.set(val);}
    public double getOrtalama() {return ortalama.get();}
    public void setOrtalama(double val) {ortalama.set(val);}
    public int getMax() {return max.get();}
    public void setMax(int val) {max.set(val);}
    public int getUzunluk() {return uzunluk.get();}
    public void setUzunluk(int val) {uzunluk.set(val);}
    public int getEþdeðerSayýsý() {return eþdeðerSayýsý.get();}
    public void setEþdeðerSayýsý(int val) {eþdeðerSayýsý.set(val);}
}
