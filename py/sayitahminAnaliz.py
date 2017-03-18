# -*- coding: utf-8 -*-
"""
Created on Fri Feb 17 18:59:36 2017

@author: akadi
"""

def sayilariUret():
    S = []
    for n in range(10000):
        ss = "%04d"%n
        if ss[0]==ss[1] or ss[0]==ss[2] or ss[0]==ss[3] or ss[1]==ss[2] or ss[1]==ss[3] or ss[2]==ss[3]:
            continue
        else:
            S.append(ss)
    return S

def getMatches(sa, ta):
    A, B = 0, 0
    for i in range(len(ta)):
        if ta[i] in sa:
            if ta[i] == sa[i]:
                A += 1
            else:
                B += 1
    return A, B
    

def filt(S,tahmin,A,B):
    S1 = []
    for ss in S:
        son1 = getMatches(ss,tahmin)
        if son1[0]==A and son1[1]==B:
            S1.append(ss)
    return S1

def getHist(S,tahmin):
    # try given guess for each code in S
    # count occurances of different results, return as histogram
    hist = {}
    for ss in S:
        son = getMatches(ss,tahmin)
        a = str(son[0])+str(son[1])
        if a in hist:
            hist[a] += 1
        else:
            hist[a] = 1
    return hist

import math
def entropy(hist):
    entr = 0
    tot = 0
    for a in hist:
        tot += hist[a]
    for a in hist:
        p = 1.0*hist[a]/tot
        entr -= p*math.log(p,2)
    return entr

def printBestHist(hd):
    # hd (dict) tahmin : hist
    for t in hd:
        v = hd[t].values()
        print(t,end=" : ")
        print("en:%5.3f,av:%5.1f,ln:%3d,mx:%4d,mn:%3d" % (entropy(hd[t]), sum(v)/float(len(v)),len(v),max(v),min(v)) , end=" : "  )
        print(hd[t])
    
def analysis():
    S = sayilariUret()
    S1 = filt(S,"1234",0,0)
    hd = {}
    hc = {}
    for tah in S:
        h = getHist(S1,tah)
        found = False
        for t in hd:
            if hd[t] == h:
                found = True
                hc[t] += 1
                break
        if not found:
            hd[tah] = h
            hc[tah] = 1
    print(hc)
    printBestHist(hd)

# 0123 ilk tahmini için her alternatif sonucu analiz eder (00,01,02,...):
#    Her biri için (for ptrn in h0) filtre S1 listesini (mümkün sayı listesi) oluşturur
#    Bütün tahminleri S1'de deneyerek (for tah in S)...
#        Her bir tahmin için sonuç histogramı (h) bulur.
#        (h: S1'de tah girilince hangi sonuc kaç sayi için -hangi olasılıkla- gelir)
#        Farklı tahminler ve her biri için sonuc histogramı h, hd içinde saklanır.
#        Aynı olasılık dağılımı h'a sahip tahminler aynı sayılır, tekrar kaydedilmez.
#        Hangi tahminden kaç tane tekrar oldu (aynı h'ı veren benzer tahmin) sayılır, hc'de.
def analysis1():
    S = sayilariUret()
    h0 = {'00': 360, '01': 1440, '02': 1260, '03': 264, '04': 9,
          '10': 480, '11': 720, '12': 216, '13': 8, '20': 180, '21': 72,
          '22': 6, '30': 24, '40': 1}
    for ptrn in h0:
        S1 = filt(S,"0123",int(ptrn[0]),int(ptrn[1]))
        print("\n-------------%s [%4d]-----------" % (ptrn,len(S1)) )
        hd = {} # (dict) tahmin:hist - 1234 : {00:10,01:25,...}
        hc = {} # (dict) tahmin:freq (different tahmin gives same hist. combine them and track occurences)
        for tah in S:
            h = getHist(S1,tah)
            found = False
            for t in hd:
                if hd[t] == h:
                    found = True
                    hc[t] += 1
                    break
            if not found:
                hd[tah] = h
                hc[tah] = 1
        print(hc)
        printBestHist(hd)

def ilkHamleAnalysis():
    S = sayilariUret()
    print("\n------------- HEPSI - İLK BAŞ [%4d]-----------" % (len(S)) )
    hd = {} # (dict) tahmin:hist - 1234 : {00:10,01:25,...}
    hc = {} # (dict) tahmin:freq (different tahmin gives same hist. combine them and track occurences)
    for tah in S:
        h = getHist(S,tah)
        found = False
        for t in hd:
            if hd[t] == h:
                found = True
                hc[t] += 1
                break
        if not found:
            hd[tah] = h
            hc[tah] = 1
    print(hc)
    printBestHist(hd)

'''
Verilen tahminleri, verilen sayı listesinde dene. Sonuç histogramlarını döndür.

[Girdiler]	
S1: Olası sayıların listesi.	S : Denenecek tahminler listesi.

[Çıtılar]	
hd: {tahmin : histogram} (dict)		: tahmini ve o tahminin ürettiği sonuç histogramını içeren dict. Histogram da bir dict. 
hd: {tahmin : tekrar sayısı} (dict)	: Bir çok tahmin aynı histogramı verebilir. Bunları tekrar hd'ye ekleme, ama tekrarları say. hc bu sayıyı tutar.
'''
def tahminleriListele(S1,S):
    hd = {} # (dict) tahmin:hist - 1234 : {00:10,01:25,...}
    hc = {} # (dict) tahmin:freq (different tahmin gives same hist. combine them and track occurences)
	
	# Bütün tahminleri sırasıyla dene
	for tah in S:
        h = getHist(S1,tah)		# tah için histogram (S1 sayı listesinde kaçı 00, kaçı 01 vs. sonuç veriyor)
		# Aynı histogramı veren hd içinde hali hazırda bir tahmin var mı? 
		# Varsa hd'ye tekrar ekleme, sadece hc'de sayacını artır
        found = False
        for t in hd:
            if hd[t] == h:		# hd içindeki t tahmininin histogramı, mevcu h histogramına eşit mi?
                found = True
                hc[t] += 1
                break
		# Mevcut h histogramı hd içinde yoksa, ekle; sayacını 1 olarak hc'ye ekle
        if not found:
            hd[tah] = h
            hc[tah] = 1
    return hd, hc

def eniyiTahminSec(hd):
    enTahmin = "0000"
    enKriter = 10000
    enHist = []
    for tah in hd:
        v = hd[tah].values()
        #kriter = max(v)                     # max'ı en düşük olanı seç
        kriter = sum(v) / float(len(v))    # ortalama en düşük olanı seç
        #kriter = -entropy(hd[tah])          # entropy en yüksek olanı seç => -ent en düşük olan
        if kriter < enKriter:
            enKriter = kriter
            enTahmin = tah
            enHist = hd[tah]
    return enTahmin, enKriter, enHist

def ozcozR(S1,S,cozUzHist,level,pre,yaz=False,ofile=None):
    if yaz: print("\n*** Level: %d. Len: %d*** |%s " %(level,len(S1),pre))
    
    # tek ihtimal kaldıysa tahmin sonuçlarını araştırmaya gerek yok (eniyiTahmin doğru çalışmayabilir de)
    if len(S1)==1:
        enTahmin = S1[0]
        enHist = getHist(S1,enTahmin)
        if enHist["40"]!=1 or len(enHist)!=1:
            print("Beklenmedik durum, teke inmişti:", end=" -> ")
            print(enHist)
    else:
        # tahmin alternatiflerini çıkar (bir birinden farklı sonuç veren tahminler)
        # hd (dict): {tahmin:hist} . orn: 1234 : {00:10,01:25,...}
        # hc (dict): {tahmin:freq} (different tahmin gives same hist. combine them and track occurences)
        hd,hc = tahminleriListele(S1,S)
        
        # en iyi tahmini seç (sonuç histogramlarını karşılaştır)
        enTahmin, enKriter, enHist = eniyiTahminSec(hd)
        if yaz: print("En iyi tahmin: %s (%d)" % (enTahmin,enKriter), end=" : " )
        if yaz: print(enHist)
    
    # enTahmin'i uygulayınca olabilecek Sonuc'ları ozcoz ile takip et
    for son in enHist:
        S2 = filt(S1,enTahmin,int(son[0]),int(son[1]))
        if son=="40":
            cozUzHist[level+1] += 1
            if ofile is None:
                print("COZUM(%d): %s : %s : [%s] *" % (level+1,pre,enTahmin,son))
            else:
                ofile.write("COZUM(%d): %s : %s : [%s]\n" % (level+1,pre,enTahmin,son))
        else:
            ozcozR(S2,S,cozUzHist,level+1, "%s : %s : [%s]" % (pre,enTahmin,son), False,ofile)
    return cozUzHist    

def ozcoz():
    S = sayilariUret()
    cozUzHist = [0]*10
    ofile = None
    ofile = open("stahminAna_ozcoz_mean.txt","w")            
    # ilk tahmini burada gerçekleştir - zaman kaybetme, zaten tek alternatif var
    level = 0
    pre = ">"
    enTahmin = "0123"
    enHist = getHist(S,enTahmin)
    for son in enHist:
        S2 = filt(S,enTahmin,int(son[0]),int(son[1]))
        if len(S2)==1:
            cozUzHist[level+1] += 1
            if ofile is None:
                print("COZUM(%d): %s : %s : [%s] *" % (level+1,pre,enTahmin,son))
            else:
                ofile.write("COZUM(%d): %s : %s : [%s]\n" % (level+1,pre,enTahmin,son))
        else:
            ozcozR(S2,S,cozUzHist,level+1, "%s : %s : [%s]" % (pre,enTahmin,son), False,ofile)
    
    #cozUzHist = ozcozR(S,S,cozUzHist,0,":",True)
    print("*** BİTTİ ***")
    print(cozUzHist)
    if ofile is not None:
        for x in cozUzHist:
            ofile.write("%d, "%x)
        ofile.close()
    return cozUzHist

#analysis1()
#ilkHamleAnalysis()
ozcoz()
        