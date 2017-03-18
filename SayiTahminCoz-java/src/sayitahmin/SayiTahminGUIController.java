/**
 * Say� Tahmin Yard�m Arac�n�n as�l mant��� bu s�n�fta ger�eklenmektedir.
 * 
 * YAPILACAKLAR:
 * - E�de�er tahminler listesinin ger�eklenmesi
 * - Tahmin girdi kontrol�n�n geli�tirilmesi
 * - Analiz ve e�de�er tablosundan �ift t�klayarak tahmin se�ilmesi
 * - Olas� tahmin olarak t�m 5040 liste / mevcut olas� say�lar listesi se�imi imkan�
 * - Tahmin/sonu�'un oyun ad�mlar� tablosundan do�rudan girilmesi
 * - Oyun ad�mlar�n�n veya son ad�m�n silinebilmesi / de�i�tirilmesi
 * - Kendi say� tutma ile kendi i�inde oynama imkan� sunulmas� (yeniden ba�lat: manuel/otomatik oyun)
 */
package sayitahmin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

public class SayiTahminGUIController {
	// FXMLLoader'dan gelen de�i�kenler
    @FXML private TableView<StrCell> olas�l�klarTab;
    @FXML private TableColumn<StrCell,String> olas�l�klarCol;
    
    @FXML private TableView<TahminHist> analizTab;
    @FXML private TableColumn<TahminHist,String> analizTahCol;
    @FXML private TableColumn<TahminHist,Double> analizEntCol;
    @FXML private TableColumn<TahminHist,Integer> analizMaxCol;
    @FXML private TableColumn<TahminHist,Double> analizOrtCol;
    @FXML private TableColumn<TahminHist,Integer> analizUzCol;
    @FXML private TableColumn<TahminHist,Integer> analizEsCol;
    @FXML private TableColumn<TahminHist,String> analizHisCol;
    
    @FXML private TableView<OyunAd�m> oyunTab;
    @FXML private TableColumn<OyunAd�m,Integer> oyunNoCol;
    @FXML private TableColumn<OyunAd�m,Integer> oyunAdetCol;
    @FXML private TableColumn<OyunAd�m,String> oyunTahCol;
    @FXML private TableColumn<OyunAd�m,String> oyunSonCol;
    
    @FXML private TextField girTahmin;
    @FXML private ComboBox<String> comboSonu�;
    @FXML private Button girButon, ba�Buton;
    @FXML private Label mesajLabel;
    
    // Tablolar i�in Observable listeler
    private ObservableList<StrCell> olas�Say�larObs;
    private ObservableList<TahminHist> analizObs;
    private ObservableList<OyunAd�m> oyunObs;
	private OyunAd�m sonOyunAd�m;
	private int ad�mNo = 1;
	
	private ObservableList<String> sonu�List;
    private List<String> olas�Say�larT�m, olas�Say�lar, olas�Tahminler;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	sonu�List = FXCollections.observableArrayList("00","01","02","03","04","10","11","12","13","20","21","22","30","40");
    	comboSonu�.setItems(sonu�List);
    	comboSonu�.setValue(sonu�List.get(0));
    	
        // �lk ba�taki 5040 say� olas�l��� ile olas�l�klar listesini doldur.
        olas�Say�larT�m = olas�Say�larT�m�ret();
        // �lk ba�ta olas� say�lar, t�m listeyi i�eriyor.
        olas�Say�lar = olas�Say�larT�m;
        // �imdilik olas�Tahminler'i her zaman, ba�taki 5040 olas� say�n�n t�m�nden se�iyoruz
        // �leride sadece olas�Say�lar'dan se�me vs. denenebilir.
        olas�Tahminler = olas�Say�larT�m;	

        // TabelView'de olas�Say�lar� g�stermek i�in StrCell elemanlar� ve ObservableList'e aktar.
        olas�Say�larObs = FXCollections.observableArrayList();
        olas�l�klarTab.setItems(olas�Say�larObs);
        olas�l�klarCol.setCellValueFactory(new PropertyValueFactory<StrCell,String>("cellVal"));
        olas�Say�larObsG�ncelle();
        
        // Analiz tablosu i�in analizObs listesini olu�tur, gerekli ba�lant�lar� yap.
        // analizObs listesi g�ncellendik�e (tahmin histogramlar� eklenip ��kar�ld�k�a) tablo da g�ncellenir.
        analizObs = FXCollections.observableArrayList();
        analizTab.setItems(analizObs);
        analizTahCol.setCellValueFactory(new PropertyValueFactory<TahminHist,String>("tahmin"));
        analizEntCol.setCellValueFactory(new PropertyValueFactory<TahminHist,Double>("entropi"));
        analizMaxCol.setCellValueFactory(new PropertyValueFactory<TahminHist,Integer>("max"));
        analizOrtCol.setCellValueFactory(new PropertyValueFactory<TahminHist,Double>("ortalama"));
        analizUzCol.setCellValueFactory(new PropertyValueFactory<TahminHist,Integer>("uzunluk"));
        analizEsCol.setCellValueFactory(new PropertyValueFactory<TahminHist,Integer>("e�de�erSay�s�"));
        analizHisCol.setCellValueFactory(new PropertyValueFactory<TahminHist,String>("histStr"));
        
        analizYap();
        
        // Oyun ad�mlar�n� (olas� say� adedi, tahmin, tahminin sonucu) g�steren liste ve tablo ba�lant�s�n� haz�rla
        oyunObs = FXCollections.observableArrayList();
        oyunTab.setItems(oyunObs);
        oyunNoCol.setCellValueFactory(new PropertyValueFactory<OyunAd�m,Integer>("no"));
        oyunAdetCol.setCellValueFactory(new PropertyValueFactory<OyunAd�m,Integer>("olas�Adet"));
        oyunTahCol.setCellValueFactory(new PropertyValueFactory<OyunAd�m,String>("tahmin"));
        oyunSonCol.setCellValueFactory(new PropertyValueFactory<OyunAd�m,String>("sonu�"));

        sonOyunAd�m = new OyunAd�m(ad�mNo, olas�Say�lar.size());
        oyunObs.add(sonOyunAd�m);
        
    }

    
    /**
     * 4 basamakl�, basamak tekrar� olmayan, 0'la ba�layabilen 5040 say�y� �retip String List olarak d�nd�r�r.
     * 
     * @return 5040 olas� say�y� (4 basamak,tekrars�z,0'la ba�layabilir) String olarak i�eren bir List.
     */
	public List<String> olas�Say�larT�m�ret() {
		List<String> S = new ArrayList<>(5040);
		for (int i=0; i<10000; i++) {
			String si = String.format("%04d",i);
			if ( si.charAt(0)==si.charAt(1) || si.charAt(0)==si.charAt(2) 
					|| si.charAt(0)==si.charAt(3) || si.charAt(1)==si.charAt(2) 
					|| si.charAt(1)==si.charAt(3) || si.charAt(2)==si.charAt(3) ) {
				// basamak tekrar� oldu�u i�in bunu atla
			} else {
				S.add(si);
			}
		}
		return S;
	}

	/**
	 * Yeni bir tahmin ve sonu� girildi�inde olas�Say�lar� buna g�re g�nceller, oyunAd�m�n� ekler, analizi g�nceller.
	 * @param event
	 */
    @FXML protected void tahminSonu�Gir(ActionEvent event) {
    	String tahmin = girTahmin.getText();
    	// YAPILACAK: Daha d�zg�n girdi kontrol� yap.
    	if (tahmin.length() > 4) {
    		tahmin = tahmin.substring(0, 4);
    		girTahmin.setText(tahmin);
    		mesajLabel.setText("Girdi�iniz tahmin uzun oldu�u i�in k�rp�ld�.");
    	}
    	String sonu� = comboSonu�.getValue();
    	System.out.println("Tahmin: " + tahmin + " Sonu�: " + sonu�);
    	
    	// tahmin ve sonu� ile i�lem yap: olas�Say�lar'� filtrele. Tablo'da g�r�nen olas�Say�larObs listesini de g�ncelle. 
    	byte[] son = new byte[2];
    	son[0] = (byte) Character.digit(sonu�.charAt(0), 10);
    	son[1] = (byte) Character.digit(sonu�.charAt(1), 10);
    	olas�Say�lar = tahminSonucunaUyanOlas�Say�lar(olas�Say�lar, tahmin, son);
    	olas�Say�larObsG�ncelle();
    	
    	// ad�mNo art�r; yeni ad�mNo, olas�Adet ile oyunAd�m� olu�tur; oyunObs'a ekle.
    	sonOyunAd�m.setTahmin(tahmin);
    	sonOyunAd�m.setSonu�(sonu�);
    	ad�mNo++;
    	sonOyunAd�m = new OyunAd�m(ad�mNo,  olas�Say�lar.size());
    	oyunObs.add(sonOyunAd�m);
    	
    	// Analizi g�ncelle
    	analizYap();
    }
	
    /**
     * olas�Say�lar ve olas�Tahminler'den olas� tahmin histogramlar�n� hesapla, analizObs listesini s�f�rlay�p doldur.
     * 
     * Mevcut olas�Say�lar'� ve olas�Tahminleri (olas�Say�larT�m'�n tamam�n� olas�Tahmin olarak kullan) kullanarak
     * tahmin sonu� histogramlar�n� �ret. Bunun sonucunda analizObs listesi g�ncellenir, sonu� da GUI'deki tabloda g�r�n�r.
     * analizObs listesi, hesaplanan TahminHist nesnelerini i�erir. Ba�ta analizObs listesini s�f�rlar.
     * 
     * Tahmin sonu� histogram�: olas�Tahminlerin her biri i�in bir histogram �retilir. 
     *    olas�Tahmin, b�t�n olas�Saylar'da denenir. Her olas� say�dan gelen sonu� (00,01,02,... gibi) say�l�r.
     *    Sonu�ta her olas�Tahmin i�in bir histogram (hangi sonucun ka� defa geldi�i) elde edilir.
     *    Bu histogram, o tahminin olas�Say�lar� nas�l ay�rd���n�n (sonu�lar�n ne kadar ay�rt edici oldu�unun) bir g�stergesidir.
     * 
     * Farkl� olas�Tahmin'ler ayn� histogram� sonu� verebilir. Bunlara e�de�er tahminler diyoruz.
     * Bunlar� ayr� ayr� listelemek yerine, e�de�er tahminlerden ilkini tutup, di�erlerini onun "e�de�er" listesine ekliyoruz.
     * �rne�in ilk tahminde b�t�n tahminler e�de�erdir. Dolay�s�yla "0123" tahmini, 5040 e�de�eri olan tek alternatiftir.
     */
    private void analizYap() {
    	analizObs.clear();
        for (String tahmin : olas�Tahminler) {
        	int[] hist = tahminHistHesapla(tahmin);
        	
        	boolean yeniTahmin = true;
        	for (TahminHist mevcutTahmin : analizObs) {
        		if (mevcutTahmin.e�de�erMi(hist)) {
        			mevcutTahmin.e�de�erEkle(tahmin);
        			yeniTahmin = false;
        			break;
        		}
        	}
        	if (yeniTahmin) {
        		analizObs.add( new TahminHist(tahmin,hist) );
        	}
        }
    }
    
    /**
     * Verilen tahmin'i b�t�n olas�Say�lar'a uygulay�p, e�le�me sonu�lar�n�n histogram�n� (ka� 00,01,...vs) bulur.
     * 
     * @param tahmin	Sonu� histogram� hesaplanacak tahmin. Tahmin sonu� histogram� i�in analizYap()'a bak�n�z.
     * @return			Elde edilen sonu� histogram� int dizisi olarak d�nd�r�l�r. 
     * 					Histogram 21 uzunluktad�r. A*5 + B indeksleme form�l� kullan�l�r (A:do�ru yerde,B:yanl�� yerde)
     * 					Yani: 	"00","01","02","03","04","10","11","12","13","x14", //0-9 elemanlar
								"20","21","22","x23","x24", 						//10-14 elemanlar 
								"30","x31","x32","x33","x34","40"					//15-20 elemanlar
						23,31 gibi sonu�lar imkans�z oldu�u halde, indeksleme form�l�ne uymak i�in histogramda bulunur.
     */
    private int[] tahminHistHesapla(String tahmin) {
    	int[] hist = new int[21];
    	
    	for (String olas�Say� : olas�Say�lar) {
    		byte[] res = tahminKar��la�t�r(olas�Say�, tahmin);
			hist[ res[0]*5+res[1] ]++;
    	}
    	return hist;
    }
    
    /**
     * Verilen say� ve tahmin (String) kar��la�t�r�p ka� "do�ru yerinde-A", ka� "yanl�� yerde-B" do�ru basamak oldu�unu bulur.
     * 
     * @param say�		String olarak verilen Say�
     * @param tahmin	String olarak verilen Tahmin
     * @return			2 elemanl� byte dizisi olarak, e�le�me sonucu. 
     * 					res[0] = A (yeri do�ru olan do�ru basamak)
     * 					res[1] = B (yeri yanl�� olan do�ru basamak)
     */
	public static byte[] tahminKar��la�t�r(String say�, String tahmin) {
		byte A = 0,B = 0;
		for (int i=0; i<tahmin.length(); i++) {
			int x = say�.indexOf(tahmin.charAt(i));
			if (x==i) A++;
			else if (x >= 0) B++;
		}
		byte[] res = {A,B};
		return res;
	}
    
	/**
	 * Verilen tahmin ve sonu�'a g�re olas� say�lar listesini filtreler. Sonucu yeni bir liste olarak d�nd�r�r.
	 * Verilen Sin olas� say�lar listesindeki her say�y� tahmin ile kar��la�t�r�p, verilen res sonucunu verip vermedi�ini kontrol eder.
	 * Verilen res sonucuna uyan say�lar� Sout listesine atar ve bu yeni listeyi d�nd�r�r.
	 *  
	 * @param Sin		Ba�taki olas� say�lar listesi
	 * @param tahmin	Girilen tahmin ve...
	 * @param res		girilen tahmin'e kar��l�k al�nan e�le�me sonuc (00, 01,...,40)
	 * @return			Verilen olas� say�lar i�inde, verilen tahmin ve sonuca uyan yeni olas� say�lar listesi.
	 */
	public static List<String> tahminSonucunaUyanOlas�Say�lar(List<String> Sin, String tahmin, byte[] res) {
		List<String> Sout = new ArrayList<>();
		for (String say� : Sin) {
			byte[] r = tahminKar��la�t�r(say�, tahmin);
			if (r[0]==res[0] && r[1]==res[1]) {
				Sout.add(say�);
			}
		}
		return Sout;
	}
	
	/**
	 * olas�Say�larObs listesini silip olas�Say�lar listesinden tekrar doldurur.
	 * olas�Say�lar yeni bir tahmin ile g�ncellendi�inde bu �ekilde Tablo'da g�r�nen olas�Say�larObs listesi de g�ncellenir.
	 */
	private void olas�Say�larObsG�ncelle() {
		olas�Say�larObs.clear();
        for (String s : olas�Say�lar) {olas�Say�larObs.add(new StrCell(s));}
	}

	/**
	 * olas�Say�lar�, oyun ge�mi�ini ve analiz sonu�lar�n� s�f�rlayarak yeni oyun ba�lat�r.
	 * @param event
	 */
    @FXML protected void yeniBa�tan(ActionEvent event) {
    	olas�Say�lar = olas�Say�larT�m;
    	olas�Say�larObsG�ncelle();
    	
    	ad�mNo = 1;
    	sonOyunAd�m = new OyunAd�m(ad�mNo,  olas�Say�lar.size());
    	oyunObs.clear();
    	oyunObs.add(sonOyunAd�m);
    	
    	analizYap();
    }
}