# -*- coding: utf-8 -*-
"""
Created on Mon Dec 12 11:39:36 2016

@author: WİN7
"""

import random

DIGSTR = "0123456789"
SLEN = 4    # length of the random string
counts = []

##########################################

def parseTahmin(tStr):
    if len(tStr) != SLEN:
        print("Dört basamak olmalı.")
        return []
    t = []
    for ch in tStr:
        if not ch in DIGSTR:
            print("Geçersiz basamak: " + ch)
            return []
        if ch in t:
            print("Tekrarlanan basamak:" + ch)
            return []
        t.append(ch)
    return t

def getMatches(sa, ta):
    A, B = 0, 0
    for i in range(len(ta)):
        if ta[i] in sa:
            if ta[i] == sa[i]:
                A += 1
            else:
                B += 1
    return A, B
    
def showAB(tLST, aLST):
    for i in range(len(tLST)):
        print( "%d. %s : %d A,  %d B" % (i+1, tLST[i], aLST[i][0], aLST[i][1]))
        
##########################################
oyunNo = 0
while True:
	# Rastgele sayı üret
	# Verilen karakter listesinden (DIGSTR) istenilen uzunlukta (SLEN), tekrarsız bir karakter dizisi
    sayi = random.sample(DIGSTR, SLEN)
    
    tahminSay = 1
    A, B = 0, 0
    oyunNo += 1
    tahminLST = []
    ABLST = []
    
    print("*** Oyun : %d ***" % (oyunNo))
    while A != SLEN:
        tahminStr = input("Tahmin? [Oyun %d. Tahmin %d]: " % (oyunNo,tahminSay))
        if tahminStr == "quit" or tahminStr == "exit":
            exit

        tahmin = parseTahmin(tahminStr)
        if len(tahmin) < SLEN:
            print("Geçersiz tahmin")
            continue
        else:
            tahminSay += 1
            A,B = getMatches(sayi, tahmin)
            tahminLST.append(tahminStr)
            ABLST.append( (A,B) )
            #print( "%s : %d A,  %d B" % (tahminStr, A, B))
            showAB(tahminLST, ABLST)
    
    if A == SLEN:
        print("Tebrikler! %d denemede buldunuz." % (tahminSay))
        counts.append(tahminSay)
        ort = 1.0*sum(counts)/len(counts)
        med = sorted(counts)[len(counts)//2]
        print("%d oyunda ortalamanız: %f (medyan %f)" % (len(counts), ort, med))  
        
    cvp = input("Tekrar için 'e' girin: ")
    if cvp != "e":
        break
    
print("Oyun bitti.")
