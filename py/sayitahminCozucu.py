# -*- coding: utf-8 -*-
"""
Created on Sat Feb 11 14:10:30 2017

@author: WİN7
"""

# 4 basamaklı, basamakları tekrarsız sayıların (4lü karakter dizisi) 
#    tümünü bir liste olarak (5040 tane) üretip döndürür.
def sayilariUret():
    S = []
    for n in range(10000):
        ss = "%04d"%n
        if ss[0]==ss[1] or ss[0]==ss[2] or ss[0]==ss[3] or ss[1]==ss[2] or ss[1]==ss[3] or ss[2]==ss[3]:
            continue
        else:
            S.append(ss)
    return S

# Verilen tahmin'i sayi ile karşılaştırıp kaç A (doğru yerinde) kaç B (yanlış yerde) doğru karakter var döndürür.
def getMatches(sayi, tahmin):
    A, B = 0, 0
    for i in range(len(tahmin)):
        if tahmin[i] in sayi:
            if tahmin[i] == sayi[i]:
                A += 1
            else:
                B += 1
    return A, B
    
# Verilen S sayi listesi içinde, verilen tahmin'e verilen AB sonucunu veren bütün sayilari S1 listesi olarak döndürür.
# 	Yani, tahmin ve alınan sonuçla uyumlu olan sayilari bulur, orijinal alternatif listesini filtrelemiş olur.
def filt(S,tahmin,A,B):
    S1 = []
    for ss in S:
        son1 = getMatches(ss,tahmin)
        if son1[0]==A and son1[1]==B:
            S1.append(ss)
    return S1

# Verilen S listesindeki her sayi için verilen tahmin'i dener.
# Her alternatif sonuçtan (AB değeri) kaç tane alındığını kaydeder, hist sözlüğünde döndürür.
# hist (sözlük) : {sonuç : kaç_defa} . Örn: {"00":70, "01":45, "30":5, "40":1}
def getHist(S,tahmin):
    # try given guess for each code in S
    # count occurances of different results, return as hist
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
def getEntropies(S):
    E = [0]*10000
    # try all possible guesses (n) and find entropy of results for each guess
    for n in range(10000):
        tahmin = "%04d"%n
        hist = getHist(S,tahmin)
        tot = 0
        for a in hist:
            tot += hist[a]
        entr = 0
        for a in hist:
            p = 1.0*hist[a]/tot
            entr -= p*math.log(p,2)
        E[n] = entr
        if entr > 3.5: print("%04d : %f"%(n,entr))
    return E
    
def getEntropiesDict(S,UNI,yaz=True,tr=1.9):
    EN = OrderedDict()
    # try all possible guesses (n) and find entropy of results for each guess
    for n in range(len(UNI)):
        #tahmin = "%04d"%n
        tahmin = UNI[n]
        hist = getHist(S,tahmin)
        tot = 0
        for a in hist:
            tot += hist[a]
        entr = 0
        for a in hist:
            p = 1.0*hist[a]/tot
            entr -= p*math.log(p,2)
        EN[tahmin] = entr
        if yaz and entr >= tr: 
            print("%s : %f"%(tahmin,entr), end=", ")
            print(hist)
        if yaz and "40" in hist:
            print("Possible guess:%s : %f"%(tahmin,entr), end=", ")
            print(hist)
    return EN
    
def ENInfo(EN):
    mt = max(EN, key=lambda t: EN[t])
    cnt = 0
    for t in EN:
        if (EN[t]==EN[mt]): cnt += 1
    print("Max: %s -> %f. (%d times)" % (mt,EN[mt],cnt))
    
def ENSortInfo(EN,top=10):
    ENs = sorted(EN,key=lambda t: EN[t], reverse=True)
    for i in range(top):
        print("%d. %s -> %f"%(i,ENs[i],EN[ENs[i]]))
    
from collections import OrderedDict    
def game():
    tahs = OrderedDict()
    tahs["1234"] = (1,1)
    tahs["0235"] = (1,1)
    tahs["2637"] = (2,1)
    tahs["2738"] = (1,1)
    #tahs["9786"] = (0,1)
    S = sayilariUret()
    print("Starting with: %d"%len(S))
    S0 = S
    cnt = 0
    for t in tahs:
        res = tahs[t]
        S1 = filt(S0,t,res[0],res[1])
        cnt += 1
        S0 = S1
        print("S%d : tahmin: %s, res: %d%d, len: %d"%(cnt,t,res[0],res[1],len(S1)))
    if len(S1) < 20:
        print("Listing remaining candidates:")
        for t in S1:
            print(t,end=" -> ")
            print(getHist(S1,t))
    EN = getEntropiesDict(S1,S, False)
    return EN

def game1():
    history = OrderedDict()
    S = sayilariUret()
    print("Starting with: %d"%len(S))
    S0 = S
    cnt = 1
    tahmin = "1234"
    while tahmin != "":
        print("-"*20)
        restr = input( "%d. Result for %s? "%(cnt, tahmin) )
        history[tahmin] = restr
        res = ( int(restr[0]), int(restr[1]) )
        S1 = filt( S0, tahmin, res[0], res[1] )
        cnt += 1
        S0 = S1
        print("tahmin: %s, res: %d%d, len: %d"%(tahmin,res[0],res[1],len(S1)))
        if len(S1) < 20:
            print("Listing remaining candidates:")
            for t in S1:
                print(t,end=" -> ")
                print(getHist(S1,t))

        EN = getEntropiesDict(S1,S, False)
        ENInfo(EN)
        ENSortInfo(EN,15)
        tahmin = input("Next tahmin? ")
    print(history)
    return history


def dene0():    
    S = sayilariUret()
    print(len(S))
    S1 = filt(S,"1234",0,2)
    print(len(S1))
    S2 = filt(S1,"5678",0,2)
    print(len(S2))
    S3 = filt(S2,"7812",0,2)
    print(len(S3))
    #E3 = getEntropies1(S3,S)
    S4 = filt(S3,"2370",0,3)
    print(len(S4))
    #E4 = getEntropies1(S4,S)
    S5 = filt(S4,"9786",0,1)
    print(len(S5))
    E5 = getEntropies1(S5,S)

#EN = game()
#ENInfo(EN)
#ENSortInfo(EN,15)
game1()