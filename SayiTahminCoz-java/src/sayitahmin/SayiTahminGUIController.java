/**
 * Sayý Tahmin Yardým Aracýnýn asýl mantýðý bu sýnýfta gerçeklenmektedir.
 * 
 * YAPILACAKLAR:
 * - Eþdeðer tahminler listesinin gerçeklenmesi
 * - Tahmin girdi kontrolünün geliþtirilmesi
 * - Analiz ve eþdeðer tablosundan çift týklayarak tahmin seçilmesi
 * - Olasý tahmin olarak tüm 5040 liste / mevcut olasý sayýlar listesi seçimi imkaný
 * - Tahmin/sonuç'un oyun adýmlarý tablosundan doðrudan girilmesi
 * - Oyun adýmlarýnýn veya son adýmýn silinebilmesi / deðiþtirilmesi
 * - Kendi sayý tutma ile kendi içinde oynama imkaný sunulmasý (yeniden baþlat: manuel/otomatik oyun)
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
	// FXMLLoader'dan gelen deðiþkenler
    @FXML private TableView<StrCell> olasýlýklarTab;
    @FXML private TableColumn<StrCell,String> olasýlýklarCol;
    
    @FXML private TableView<TahminHist> analizTab;
    @FXML private TableColumn<TahminHist,String> analizTahCol;
    @FXML private TableColumn<TahminHist,Double> analizEntCol;
    @FXML private TableColumn<TahminHist,Integer> analizMaxCol;
    @FXML private TableColumn<TahminHist,Double> analizOrtCol;
    @FXML private TableColumn<TahminHist,Integer> analizUzCol;
    @FXML private TableColumn<TahminHist,Integer> analizEsCol;
    @FXML private TableColumn<TahminHist,String> analizHisCol;
    
    @FXML private TableView<OyunAdým> oyunTab;
    @FXML private TableColumn<OyunAdým,Integer> oyunNoCol;
    @FXML private TableColumn<OyunAdým,Integer> oyunAdetCol;
    @FXML private TableColumn<OyunAdým,String> oyunTahCol;
    @FXML private TableColumn<OyunAdým,String> oyunSonCol;
    
    @FXML private TextField girTahmin;
    @FXML private ComboBox<String> comboSonuç;
    @FXML private Button girButon, baþButon;
    @FXML private Label mesajLabel;
    
    // Tablolar için Observable listeler
    private ObservableList<StrCell> olasýSayýlarObs;
    private ObservableList<TahminHist> analizObs;
    private ObservableList<OyunAdým> oyunObs;
	private OyunAdým sonOyunAdým;
	private int adýmNo = 1;
	
	private ObservableList<String> sonuçList;
    private List<String> olasýSayýlarTüm, olasýSayýlar, olasýTahminler;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	sonuçList = FXCollections.observableArrayList("00","01","02","03","04","10","11","12","13","20","21","22","30","40");
    	comboSonuç.setItems(sonuçList);
    	comboSonuç.setValue(sonuçList.get(0));
    	
        // Ýlk baþtaki 5040 sayý olasýlýðý ile olasýlýklar listesini doldur.
        olasýSayýlarTüm = olasýSayýlarTümÜret();
        // Ýlk baþta olasý sayýlar, tüm listeyi içeriyor.
        olasýSayýlar = olasýSayýlarTüm;
        // Þimdilik olasýTahminler'i her zaman, baþtaki 5040 olasý sayýnýn tümünden seçiyoruz
        // Ýleride sadece olasýSayýlar'dan seçme vs. denenebilir.
        olasýTahminler = olasýSayýlarTüm;	

        // TabelView'de olasýSayýlarý göstermek için StrCell elemanlarý ve ObservableList'e aktar.
        olasýSayýlarObs = FXCollections.observableArrayList();
        olasýlýklarTab.setItems(olasýSayýlarObs);
        olasýlýklarCol.setCellValueFactory(new PropertyValueFactory<StrCell,String>("cellVal"));
        olasýSayýlarObsGüncelle();
        
        // Analiz tablosu için analizObs listesini oluþtur, gerekli baðlantýlarý yap.
        // analizObs listesi güncellendikçe (tahmin histogramlarý eklenip çýkarýldýkça) tablo da güncellenir.
        analizObs = FXCollections.observableArrayList();
        analizTab.setItems(analizObs);
        analizTahCol.setCellValueFactory(new PropertyValueFactory<TahminHist,String>("tahmin"));
        analizEntCol.setCellValueFactory(new PropertyValueFactory<TahminHist,Double>("entropi"));
        analizMaxCol.setCellValueFactory(new PropertyValueFactory<TahminHist,Integer>("max"));
        analizOrtCol.setCellValueFactory(new PropertyValueFactory<TahminHist,Double>("ortalama"));
        analizUzCol.setCellValueFactory(new PropertyValueFactory<TahminHist,Integer>("uzunluk"));
        analizEsCol.setCellValueFactory(new PropertyValueFactory<TahminHist,Integer>("eþdeðerSayýsý"));
        analizHisCol.setCellValueFactory(new PropertyValueFactory<TahminHist,String>("histStr"));
        
        analizYap();
        
        // Oyun adýmlarýný (olasý sayý adedi, tahmin, tahminin sonucu) gösteren liste ve tablo baðlantýsýný hazýrla
        oyunObs = FXCollections.observableArrayList();
        oyunTab.setItems(oyunObs);
        oyunNoCol.setCellValueFactory(new PropertyValueFactory<OyunAdým,Integer>("no"));
        oyunAdetCol.setCellValueFactory(new PropertyValueFactory<OyunAdým,Integer>("olasýAdet"));
        oyunTahCol.setCellValueFactory(new PropertyValueFactory<OyunAdým,String>("tahmin"));
        oyunSonCol.setCellValueFactory(new PropertyValueFactory<OyunAdým,String>("sonuç"));

        sonOyunAdým = new OyunAdým(adýmNo, olasýSayýlar.size());
        oyunObs.add(sonOyunAdým);
        
    }

    
    /**
     * 4 basamaklý, basamak tekrarý olmayan, 0'la baþlayabilen 5040 sayýyý üretip String List olarak döndürür.
     * 
     * @return 5040 olasý sayýyý (4 basamak,tekrarsýz,0'la baþlayabilir) String olarak içeren bir List.
     */
	public List<String> olasýSayýlarTümÜret() {
		List<String> S = new ArrayList<>(5040);
		for (int i=0; i<10000; i++) {
			String si = String.format("%04d",i);
			if ( si.charAt(0)==si.charAt(1) || si.charAt(0)==si.charAt(2) 
					|| si.charAt(0)==si.charAt(3) || si.charAt(1)==si.charAt(2) 
					|| si.charAt(1)==si.charAt(3) || si.charAt(2)==si.charAt(3) ) {
				// basamak tekrarý olduðu için bunu atla
			} else {
				S.add(si);
			}
		}
		return S;
	}

	/**
	 * Yeni bir tahmin ve sonuç girildiðinde olasýSayýlarý buna göre günceller, oyunAdýmýný ekler, analizi günceller.
	 * @param event
	 */
    @FXML protected void tahminSonuçGir(ActionEvent event) {
    	String tahmin = girTahmin.getText();
    	// YAPILACAK: Daha düzgün girdi kontrolü yap.
    	if (tahmin.length() > 4) {
    		tahmin = tahmin.substring(0, 4);
    		girTahmin.setText(tahmin);
    		mesajLabel.setText("Girdiðiniz tahmin uzun olduðu için kýrpýldý.");
    	}
    	String sonuç = comboSonuç.getValue();
    	System.out.println("Tahmin: " + tahmin + " Sonuç: " + sonuç);
    	
    	// tahmin ve sonuç ile iþlem yap: olasýSayýlar'ý filtrele. Tablo'da görünen olasýSayýlarObs listesini de güncelle. 
    	byte[] son = new byte[2];
    	son[0] = (byte) Character.digit(sonuç.charAt(0), 10);
    	son[1] = (byte) Character.digit(sonuç.charAt(1), 10);
    	olasýSayýlar = tahminSonucunaUyanOlasýSayýlar(olasýSayýlar, tahmin, son);
    	olasýSayýlarObsGüncelle();
    	
    	// adýmNo artýr; yeni adýmNo, olasýAdet ile oyunAdýmý oluþtur; oyunObs'a ekle.
    	sonOyunAdým.setTahmin(tahmin);
    	sonOyunAdým.setSonuç(sonuç);
    	adýmNo++;
    	sonOyunAdým = new OyunAdým(adýmNo,  olasýSayýlar.size());
    	oyunObs.add(sonOyunAdým);
    	
    	// Analizi güncelle
    	analizYap();
    }
	
    /**
     * olasýSayýlar ve olasýTahminler'den olasý tahmin histogramlarýný hesapla, analizObs listesini sýfýrlayýp doldur.
     * 
     * Mevcut olasýSayýlar'ý ve olasýTahminleri (olasýSayýlarTüm'ün tamamýný olasýTahmin olarak kullan) kullanarak
     * tahmin sonuç histogramlarýný üret. Bunun sonucunda analizObs listesi güncellenir, sonuç da GUI'deki tabloda görünür.
     * analizObs listesi, hesaplanan TahminHist nesnelerini içerir. Baþta analizObs listesini sýfýrlar.
     * 
     * Tahmin sonuç histogramý: olasýTahminlerin her biri için bir histogram üretilir. 
     *    olasýTahmin, bütün olasýSaylar'da denenir. Her olasý sayýdan gelen sonuç (00,01,02,... gibi) sayýlýr.
     *    Sonuçta her olasýTahmin için bir histogram (hangi sonucun kaç defa geldiði) elde edilir.
     *    Bu histogram, o tahminin olasýSayýlarý nasýl ayýrdýðýnýn (sonuçlarýn ne kadar ayýrt edici olduðunun) bir göstergesidir.
     * 
     * Farklý olasýTahmin'ler ayný histogramý sonuç verebilir. Bunlara eþdeðer tahminler diyoruz.
     * Bunlarý ayrý ayrý listelemek yerine, eþdeðer tahminlerden ilkini tutup, diðerlerini onun "eþdeðer" listesine ekliyoruz.
     * Örneðin ilk tahminde bütün tahminler eþdeðerdir. Dolayýsýyla "0123" tahmini, 5040 eþdeðeri olan tek alternatiftir.
     */
    private void analizYap() {
    	analizObs.clear();
        for (String tahmin : olasýTahminler) {
        	int[] hist = tahminHistHesapla(tahmin);
        	
        	boolean yeniTahmin = true;
        	for (TahminHist mevcutTahmin : analizObs) {
        		if (mevcutTahmin.eþdeðerMi(hist)) {
        			mevcutTahmin.eþdeðerEkle(tahmin);
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
     * Verilen tahmin'i bütün olasýSayýlar'a uygulayýp, eþleþme sonuçlarýnýn histogramýný (kaç 00,01,...vs) bulur.
     * 
     * @param tahmin	Sonuç histogramý hesaplanacak tahmin. Tahmin sonuç histogramý için analizYap()'a bakýnýz.
     * @return			Elde edilen sonuç histogramý int dizisi olarak döndürülür. 
     * 					Histogram 21 uzunluktadýr. A*5 + B indeksleme formülü kullanýlýr (A:doðru yerde,B:yanlýþ yerde)
     * 					Yani: 	"00","01","02","03","04","10","11","12","13","x14", //0-9 elemanlar
								"20","21","22","x23","x24", 						//10-14 elemanlar 
								"30","x31","x32","x33","x34","40"					//15-20 elemanlar
						23,31 gibi sonuçlar imkansýz olduðu halde, indeksleme formülüne uymak için histogramda bulunur.
     */
    private int[] tahminHistHesapla(String tahmin) {
    	int[] hist = new int[21];
    	
    	for (String olasýSayý : olasýSayýlar) {
    		byte[] res = tahminKarþýlaþtýr(olasýSayý, tahmin);
			hist[ res[0]*5+res[1] ]++;
    	}
    	return hist;
    }
    
    /**
     * Verilen sayý ve tahmin (String) karþýlaþtýrýp kaç "doðru yerinde-A", kaç "yanlýþ yerde-B" doðru basamak olduðunu bulur.
     * 
     * @param sayý		String olarak verilen Sayý
     * @param tahmin	String olarak verilen Tahmin
     * @return			2 elemanlý byte dizisi olarak, eþleþme sonucu. 
     * 					res[0] = A (yeri doðru olan doðru basamak)
     * 					res[1] = B (yeri yanlýþ olan doðru basamak)
     */
	public static byte[] tahminKarþýlaþtýr(String sayý, String tahmin) {
		byte A = 0,B = 0;
		for (int i=0; i<tahmin.length(); i++) {
			int x = sayý.indexOf(tahmin.charAt(i));
			if (x==i) A++;
			else if (x >= 0) B++;
		}
		byte[] res = {A,B};
		return res;
	}
    
	/**
	 * Verilen tahmin ve sonuç'a göre olasý sayýlar listesini filtreler. Sonucu yeni bir liste olarak döndürür.
	 * Verilen Sin olasý sayýlar listesindeki her sayýyý tahmin ile karþýlaþtýrýp, verilen res sonucunu verip vermediðini kontrol eder.
	 * Verilen res sonucuna uyan sayýlarý Sout listesine atar ve bu yeni listeyi döndürür.
	 *  
	 * @param Sin		Baþtaki olasý sayýlar listesi
	 * @param tahmin	Girilen tahmin ve...
	 * @param res		girilen tahmin'e karþýlýk alýnan eþleþme sonuc (00, 01,...,40)
	 * @return			Verilen olasý sayýlar içinde, verilen tahmin ve sonuca uyan yeni olasý sayýlar listesi.
	 */
	public static List<String> tahminSonucunaUyanOlasýSayýlar(List<String> Sin, String tahmin, byte[] res) {
		List<String> Sout = new ArrayList<>();
		for (String sayý : Sin) {
			byte[] r = tahminKarþýlaþtýr(sayý, tahmin);
			if (r[0]==res[0] && r[1]==res[1]) {
				Sout.add(sayý);
			}
		}
		return Sout;
	}
	
	/**
	 * olasýSayýlarObs listesini silip olasýSayýlar listesinden tekrar doldurur.
	 * olasýSayýlar yeni bir tahmin ile güncellendiðinde bu þekilde Tablo'da görünen olasýSayýlarObs listesi de güncellenir.
	 */
	private void olasýSayýlarObsGüncelle() {
		olasýSayýlarObs.clear();
        for (String s : olasýSayýlar) {olasýSayýlarObs.add(new StrCell(s));}
	}

	/**
	 * olasýSayýlarý, oyun geçmiþini ve analiz sonuçlarýný sýfýrlayarak yeni oyun baþlatýr.
	 * @param event
	 */
    @FXML protected void yeniBaþtan(ActionEvent event) {
    	olasýSayýlar = olasýSayýlarTüm;
    	olasýSayýlarObsGüncelle();
    	
    	adýmNo = 1;
    	sonOyunAdým = new OyunAdým(adýmNo,  olasýSayýlar.size());
    	oyunObs.clear();
    	oyunObs.add(sonOyunAdým);
    	
    	analizYap();
    }
}