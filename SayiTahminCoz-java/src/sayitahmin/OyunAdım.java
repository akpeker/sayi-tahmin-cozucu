/**
 *  Say� Tahmin Yard�m Arac� GUI'sinde, oyun ad�mlar� tablosundaki verileri saklar.
 */

package sayitahmin;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class OyunAd�m {
	
	private SimpleIntegerProperty no, olas�Adet;
	private SimpleStringProperty tahmin, sonu�;
	
	public OyunAd�m(int ad�mNo, int adet) {
		no = new SimpleIntegerProperty(ad�mNo);
		olas�Adet = new SimpleIntegerProperty(adet);
		tahmin = new SimpleStringProperty("");
		sonu� = new SimpleStringProperty("");
	}
	
    public int getNo() {return no.get();}
    public void setNo(int val) {no.set(val);}
    public int getOlas�Adet() {return olas�Adet.get();}
    public void setOlas�Adet(int val) {olas�Adet.set(val);}
    public String getTahmin() {
    	 System.out.println("OyunAd�m tahmin okundu:" + tahmin.get());
    	return tahmin.get();}
    public void setTahmin(String val) {tahmin.set(val); System.out.println("OyunAd�m tahmin val="+val);}
    public String getSonu�() {return sonu�.get();}
    public void setSonu�(String val) {sonu�.set(val);}

}