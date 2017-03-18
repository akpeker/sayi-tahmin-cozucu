/**
 *  Sayı Tahmin Yardım Aracı GUI'sinde, oyun adımları tablosundaki verileri saklar.
 */

package sayitahmin;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class OyunAdım {
	
	private SimpleIntegerProperty no, olasıAdet;
	private SimpleStringProperty tahmin, sonuç;
	
	public OyunAdım(int adımNo, int adet) {
		no = new SimpleIntegerProperty(adımNo);
		olasıAdet = new SimpleIntegerProperty(adet);
		tahmin = new SimpleStringProperty("");
		sonuç = new SimpleStringProperty("");
	}
	
    public int getNo() {return no.get();}
    public void setNo(int val) {no.set(val);}
    public int getOlasıAdet() {return olasıAdet.get();}
    public void setOlasıAdet(int val) {olasıAdet.set(val);}
    public String getTahmin() {
    	 System.out.println("OyunAdım tahmin okundu:" + tahmin.get());
    	return tahmin.get();}
    public void setTahmin(String val) {tahmin.set(val); System.out.println("OyunAdım tahmin val="+val);}
    public String getSonuç() {return sonuç.get();}
    public void setSonuç(String val) {sonuç.set(val);}

}