/**
 * Say� Tahmin Yard�m Arac� GUI'sinde, Analiz tablosunda g�sterilen tahmin histogram� verilerini hesaplayan/saklayan s�n�f.
 * 
 * Histogram s�n�fa d��ar�dan verilir. Entropi, max, ortalama, uzunluk vb. bu s�n�fta hesaplan�r.
 * 
 * Ayn� zamanda histogram e�itli�i kontrol� i�in metot sunar.
 * E�de�er tahminler listesi bulundurur.
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
    public List<String> e�de�erler;
    private SimpleIntegerProperty e�de�erSay�s�;
    
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
		
		e�de�erler = new ArrayList<String>();
		e�de�erler.add(tahmin);		// Tahminin kendisini de e�de�er k�mesinde dahil edelim.
		e�de�erSay�s� = new SimpleIntegerProperty(e�de�erler.size());
	}
	
	/**
	 * Verilen hist2 histogram�n�n, bu nesnenin histogram�na e�it olup olmad��� boolean olarak d�nd�r�l�r.
	 * Bu nesnenin histogram� uzunluk olarak temel al�n�r. hist2 daha k�sa ise false d�nd�r�r. Daha uzunsa, fazlal�k k�sm� dikkate al�nmaz.
	 * Uzunluk e�itlik �art� aramama sebebi, uzun ama 0 dolu histogramlar i�in kap�y� bir derece a��k b�rakmak.
	 * 
	 * @param hist2		E�de�erli�i kar��lat�r�lacak histogram (int dizisi). En az bu nesnenin histogram� kadar uzun olmal�.
	 * @return			Histogramlar  e�itse true, de�ilse false d�nd�r�r.
	 */
	public boolean e�de�erMi(int[] hist2) {
		if (hist2.length < hist.length) return false;
		for (int i=0; i<hist.length; i++) {
			if (hist[i] != hist2[i]) return false;
		}
		return true;
	}

	/**
	 * Verilen th2 TahminHist nesnesinin, bu nesnenin histogram�na e�it olup olmad��� boolean olarak d�nd�r�l�r.
	 * Bu nesnenin histogram� uzunluk olarak temel al�n�r. th2'nin histogram� daha k�sa ise false d�nd�r�r. Daha uzunsa, fazlal�k k�sm� dikkate al�nmaz.
	 * Uzunluk e�itlik �art� aramama sebebi, uzun ama 0 dolu histogramlar i�in kap�y� bir derece a��k b�rakmak.
	 * 
	 * @param th2		E�de�erli�i kar��lat�r�lacak histogram (TahminHist). En az bu nesnenin histogram� kadar uzun olmal�.
	 * @return			Histogramlar  e�itse true, de�ilse false d�nd�r�r.
	 */
	public boolean e�de�erMi(TahminHist th2) {
		return e�de�erMi(th2.hist);
	}

	/**
	 * Verilen tahmin'i, bu tahmin'in e�de�erleri listesine ekler, e�de�erSay�s�'n� 1 art�r�r.
	 * Tahmin2'nin e�de�er olup olmad��� �nceden, e�de�erMi(int[] hist2) metoduyla kontrol edilmelidir.
	 * 
	 * @param tahmin2	E�de�er listesine eklenecek tahmin (String)
	 */
	public void e�de�erEkle(String tahmin2) {
		e�de�erler.add(tahmin2);
		e�de�erSay�s�.set(e�de�erler.size());
		//System.out.println("E�de�er: " + tahmin + " == " + tahmin2 + " (" + e�de�erSay�s�.get() + ")");
	}
	
	/**
	 * Bu histogram�n entopisini hesaplar: 
	 * 		Entropi = -toplam(pi*log(pi)), 
	 * 		pi = i'inci olas�l�k de�eri.	pi = hist[i]/toplam(hist)
	 * 
	 * Log taban 2 kullan�l�r. Bu �ekilde hesaplanan entropi birimi bit olarak ge�er (�rn: ... bit'lik bilgi).
	 * 
	 * Entropi, bu olas�l�k da��l�m�n�n ne kadar belirsizlik i�erdi�inin bir g�stergesidir. 
	 * Belirsizlik ne kadar y�ksekse, bu deneyin yap�lmas� da o kadar �ok bilgi kazand�r�r.
	 * �rne�in yaz�/tura deneyinde yaz� gelme ihtimali 1 ise, o yaz�/tura deneyi bize bir bilgi kazand�rmaz; sonucu zaten biliyoruz.
	 * Yaz�/tura atmada en y�ksek bilgi kazan�m�, e�er olas�l�klar 0.5/0.5 e�it ise olur; bu durumda belirsizlik en y�ksektir ve paray� atmak bize en �ok bilgiyi kazand�r�r.
	 * 
	 * Histogram toplam� daha �nce hesapland��� i�in bu metoda arg�man olarak g�nderilmesi tercih edildi.
	 *  
	 * @param top	Histogram elemanlar�n�n toplam�
	 * @return		Entropi (2'lik tabana g�re)
	 */
	private double entropiHesapla(int top) {
		double e = 0;
		double log2 = Math.log(2); // ln'den log2'ye �evirmek i�in
		for (int x : this.hist) {
			if (x > 0) {
				double p = (double) x / top;
				e -= p*Math.log(p)/log2;
			}
		}
		return e;
	}
	
	/**
	 * Bu histogram�, ��kt�ya uygun bir String olarak d�nd�r�r.
	 * 
	 * @return		00:123,01:72,02:35,... �eklinde String. 0 olan elemanlar dahil edilmez.
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
    public int getE�de�erSay�s�() {return e�de�erSay�s�.get();}
    public void setE�de�erSay�s�(int val) {e�de�erSay�s�.set(val);}
}
