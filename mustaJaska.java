import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Random;

public class mustaJaska {

	private static final Scanner READ = new Scanner(System.in);
	private static final Random random = new Random();

	// Alkuperäiset pakat
	//
	private static char korttienSymbolit[][] = { {
		'A','2','3','4','5','6','7','8','9','T','J','Q','K',
		'A','2','3','4','5','6','7','8','9','T','J','Q','K',
		'A','2','3','4','5','6','7','8','9','T','J','Q','K',
		'A','2','3','4','5','6','7','8','9','T','J','Q','K'
	}, {
		'♥','♥','♥','♥','♥','♥','♥','♥','♥','♥','♥','♥','♥',
		'♠','♠','♠','♠','♠','♠','♠','♠','♠','♠','♠','♠','♠',
		'♣','♣','♣','♣','♣','♣','♣','♣','♣','♣','♣','♣','♣',
		'♦','♦','♦','♦','♦','♦','♦','♦','♦','♦','♦','♦','♦'
	} };

	private static int korttienArvot[] = {
			1,2,3,4,5,6,7,8,9,10,10,10,10,
			1,2,3,4,5,6,7,8,9,10,10,10,10,
			1,2,3,4,5,6,7,8,9,10,10,10,10,
			1,2,3,4,5,6,7,8,9,10,10,10,10
	};

	// ctrl + shift + 7: togglaa kommentointi valituille riveille

	// Pienet hertat ja padat korvattu ässillä ja kympeillä
	// ->Enemmän erikoistilanteita, helpottaa testaamista
	// private static char korttienSymbolit[][] = {
	//		{ 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'T', 'T', 'T', 'J', 'Q', 'K', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A',
	//				'A', 'T', 'J', 'Q', 'K', 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A', '2',
	//				'3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K' },
	//		{ '♥', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', '♥', '♥', '♥', '♥', '♠', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
	//				'o', '♠', '♠', '♠', '♠', '♠', '♠', '♠', '♠', '♠', '♠', '♠', '♠', '♠', '♠', '♠', '♠', '♠', '♦', '♦',
	//				'♦', '♦', '♦', '♦', '♦', '♦', '♦', '♦', '♦', '♦', '♦' } };

	// private static int korttienArvot[] = { 
	//		1, 1, 1, 1, 1, 1, 1, 10, 10, 10, 10, 10, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 10,
	//		10, 10, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10 };


	private static int delay = 0;//viive, joka lisätään vakioviiveeseen
	private static boolean peliKaynnissa = false;//alustaa heittää trueksi, jakaja falseksi
	private static boolean saaVakuuttaa;
	private static double rahaa;
	private static double rahaaEnnatys = 0;
	private static int panos;
	private static String pelaaja = "";
	private static int korttiNro = 0;// tämä ++ aina kun jaetaan kortti
	private static int paikkoja = 3;// kuinka monta pelaajan paikkaa
	private static int paikatAlussa = paikkoja;// Alkuperäinen pelipaikkamäärä
	private static int vuoro = paikkoja;// vuoro pitää kirjaa siitä minkä pelikäden vuoro on käynnissä
	private static boolean vakuutus[] = new boolean[paikkoja + 1];// kädet jotka ovat true ovat vakuutettuja. [0] = käsi1 jne.
	private static boolean tuplaus[] = new boolean[paikkoja + 1];// kädet jotka ovat true ovat tuplattuja. [0] = käsi1 jne.
	private static boolean jaettu[] = new boolean[paikkoja + 1]; // kädet jotka ovat true ovat jaettuja. [1] = käsi1 jne.
	private static String paikanNimi[] = {"Talo","Pelaaja 1","Pelaaja 2","Pelaaja 3"};
	private static String paikanNimiAlussa[] = paikanNimi;//tallennetaan alkuperäiset paikan nimet tähän


	static int[][] paikka = new int[paikkoja + 1][12];
	// ensimmäiset hakasulut: pelipaikka/käsi, 0 = jakaja
	// seuraavat hakasulut: nollaindeksi = käden kokonaispisteet,
	// 1-11 indekseihin tallennetaan järjestyksessä kortit jotka ovat kädessä

	static char[][][] kuvakkeet = new char[paikkoja + 1][12][2];
	// vastaa ylempää taulukkoa
	// kolmas hakasulku: 0 = Symbolit, 1 = maat
	// esim. kuvakkeet[1][2][1] == vasemmanpuoleisen käden toisen kortin maasymboli
	// ♠ ♥ ♠ ♦

	public static void main(String[] args) {
		valikko();
		p("\n\nSuljetaan ohjelma...");
	}// main


	static void alusta() {//alustaa taulukot ja muuttujat ennen perusjakoa 

		peliKaynnissa = true;

		//resetoidaan pelipaikkojen määrä 
		paikkoja = paikatAlussa; 
		vuoro = paikatAlussa; 
		paikka = new int[paikkoja+1][12]; 
		kuvakkeet = new char[paikkoja+1][12][2]; 
		korttiNro = 0; 


		//Alustetaan jaettu, tuplaus, vakuutus taulukot 
		jaettu = new boolean[paikatAlussa+1]; 
		tuplaus = new boolean[paikatAlussa+1]; 
		vakuutus = new boolean[paikatAlussa+1]; 

		//Alustetaan pelipaikkojen nimet
		paikanNimi = paikanNimiAlussa;


	}//alusta 

	static void otsikko() {
		System.out.println("     ________________________");
		System.out.println("    |♥                      ♠|");
		System.out.println("    |  Tervetuloa pelaamaan  |");
		System.out.println("    |      MustaJaskaa       |");
		System.out.println("    |♦______________________♣|");
		System.out.println("");
	}

	static void valikko() {

		while (true) {

			otsikko();
			System.out.println("Haluatko\r\n");
			System.out.println("1. Pelaamaan\n");
			System.out.println("2. Ohjeet\r\n");
			System.out.println("3. Muokkaa pelipaikkoja\n");
			System.out.println("4. Ennätykset\n");			
			System.out.println("5. Sulje ohjelma\n");


			int vastaus = lueInt(1, 5);
			if (vastaus == 1) {
				suoritapeli();
			} else if (vastaus == 2) {
				ohjeet();
			} else if ( vastaus == 3) {
				paikkaValikko();
			} else if ( vastaus == 4) {
				ennatykset();
			} else if ( vastaus == 5) {
				break;
			}
		}//while true

	}

	static void paikkaValikko() {

		boolean takaisin = false;//tämä == true kun halutaan takaisin päävalikkoon
		int valinta = 1;//valintarivi on vakiona 1

		while (true) {

			paikkaValikkoPrint(valinta);//printataan valikko

			switch (lueInt(1,6)) {

			//lisää uusi
			case 1:
				//jos ollaan viimeisellä "< uusi paikka >" rivillä
				if (valinta>paikkoja) {
					lisaaPaikka(valinta);
					paikanNimi[valinta] = ("Pelaaja "+valinta);
				}
				else lisaaPaikka(valinta);
				break;

				//poista
			case 2:
				//jos paikkoja on enemmän kuin yksi jäljellä
				//jos ei olla viimeisellä rivillä
				if (paikkoja>1 && valinta<=paikkoja) {
					poistaPaikka(valinta);
					paikkoja--;
				}//if
				break;

				//vaihda nimi
			case 3:
				//jos ei olla viimeisellä rivillä
				if (valinta<=paikkoja) nimeaPaikka(valinta);
				break;

				//alas
			case 4:
				//jos ollaan viimeisellä rivillä mennään alkuun
				if (valinta==(paikkoja+1)) valinta = 1;
				else valinta++;
				break;

				//ylös
			case 5:
				//jos ollaan ensimmäisellä rivillä mennään loppuun
				if (valinta==1) valinta = paikkoja+1;
				else valinta--;
				break;

				//takaisin alkuvalikkoon
			case 6: 
				paikanNimiAlussa = paikanNimi;//tallennetaan nimimuutokset myös alustusmuuttujaan
				paikatAlussa = paikkoja;//tallennetaan paikkamuutokset myös apumuuttujaan
				takaisin = true;

			}//switch - käyttäjän valinta

			if (takaisin) break;

		}//while true looppi. poispääsy ainoastaan breakilla
	}//paikkaValikko

	static void nimeaPaikka (int p) {
		//argumenttiin pelipaikan numero
		//käytetään valikossa paikan nimeämiseen
		p("\n");
		while (true) {
			p("\nAnna uusi nimi pelipaikalle");
			p("\nPaikka "+p+": ");
			String tmp = READ.next();
			if (tmp.length()>10) p("\nLiian pitkä nimi: Maksimissaan kymmenen merkkiä.");
			else  {
				paikanNimi[p] = tmp;
				break;
			}//else
		}//while true
	}//nimeäPaikka


	static void paikkaValikkoPrint (int val) {
		//arugumenttiin tulee rivi jolla käyttäjä liikkuu
		//printtaa valikkoruudun
		p("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

		//printtaa pelipaikkarivit
		for (int rivi = 1;rivi<paikkoja+2;rivi++) {

			if (val==rivi) p("-->");
			else p(" "+" "+" ");

			if (rivi<paikkoja+1) p(" Paikka "+rivi+": ");
			else p(" < Lisää uusi >");

			if (rivi<paikkoja+1) p(""+paikanNimi[rivi]);

			p("\n");
		}//for - rivi

		p("\n\n");

		//printtaa käyttäjän mahdolliset valinnat
		if (val<=paikkoja) {
			p("1. Lisää uusi ");
			if (paikkoja>1) p (" 2. Poista ");
			p(" 3. Muokkaa nimeä "+" 4. Alas " + " 5. Ylös "+" 6. Takaisin");
		}//if
		//jos valinta on suurempi kuin pelipaikkojen määrä suo
		else p("1. Lisää uusi "+" 4. Alas " + " 5. Ylös " + " 6. Takaisin");

		p("\n");


	}//paikkaValikko

	static void lueTiedosto(String tiedosto) {

		try {
			FileReader lukija = new FileReader(tiedosto);	
			int i;
			while ((i = lukija.read()) != -1)
				System.out.print((char) i);
			lukija.close();
		}//try
		catch (FileNotFoundException e) {
			p("Tiedostoa ei löydy.\n");
		}
		catch (Exception e) {
			p("Tuntematon virhe tapahtui.\n");
		}
	}

	static void uusipeli() {
		System.out.println("Rahasi eivät riitä seuraavaan kierrokseen, haluatko tallettaa lisää rahaa ja pelata uudestaan?\r\n");
		System.out.println("1. Kyllä, tietenkin!");
		System.out.println("2. En, ei minun tuurillani.\r\n");
		int vastaus = lueInt(1, 2);
		if (vastaus == 1) {
			suoritapeli();
		} else if (vastaus == 2) {
			talletaEnnatys();
		}
	}

	static void talletaEnnatys() {
		System.out.println("Kiitos, että pelasit. Parempi onni ensi kerralla!\r\n");
		System.out.println("Anna nimesi, jotta voimme tallettaa tuloksesi: ");
		READ.nextLine();
		pelaaja = READ.nextLine();
		try {
			kirjoitaTiedosto();
		} catch (IOException e) {
			System.out.println("Virhe!");
		}
		System.out.println("\r\nAikaisemmat tuloksesi:\r\n");
		try {
			// tähän oma tiedostopolku johon haluaa ennätykset tallettaa
			lueTiedosto("C:\\Users\\user\\eclipse-workspace\\harkkatyö\\ennätykset.txt");
		} catch (Exception e) {
			System.out.println("Virhe!");
		}
	}

	static void suoritapeli() {
		rahaa = asetaRahaa();
		boolean lopeta = false;
		while (rahaa >= 3 && lopeta==false) {
			panos = asetaPanos();
			if (rahaa < panos * 3) {
				System.out.println("Sinulla ei ole tarpeeksi rahaa pelata.\r\n");
			} else {
				rahaa=rahaa-(panos*3);
				sekoitaPakka(korttienArvot, korttienSymbolit);
				alusta();
				perusjako();
				printtaa();
				pelaajanToiminnot();
				jakajanToiminnot();
				voittaako(panos);
				// voitonmaksu();
				if (rahaa > rahaaEnnatys) {
					rahaaEnnatys = rahaa;
				}
				if (rahaa >=3 ) {
					if (lopettaako()==true) {
						talletaEnnatys();
						lopeta = true;
					}
				}				
				vuoro = 3;
				for (int[] row : paikka) {
					Arrays.fill(row, 0);
				}
			}
		}
		if (lopeta=false) {
			uusipeli();
		}
	}

	static boolean lopettaako() {
		System.out.println("Haluatko\r\n");
		System.out.println("1. Jatkaa peliä");
		System.out.println("2. Lopettaa pelaamisen\r\n");
		int vastaus = lueInt(1, 2);
		if (vastaus == 1) {
			return false;
		} else if (vastaus == 2) {
			return true;
		} else {
			return true;
		}
	}

	static void ohjeet() {
		

		p("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		lueTiedosto("C:\\Users\\User\\eclipse-workspace\\harkkatyö\\tiedosto.txt");
		System.out.println("");
		System.out.println(" _____________________________");
		System.out.println("|                             |");
		System.out.println("| Anykey + enter: Takaisin    |");
		System.out.println("|_____________________________|\r\n");
		READ.next();//tähän jämähtää kunnes käyttäjä syöttää mitätahansa + enter
		p("\n");
		//tässä vaiheessa palataan kutsuvaan metodiin eli valikkoon
	}
	
	
	static void ennatykset() {
		
		p("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		lueTiedosto("./ennätykset.txt");
		System.out.println("");
		System.out.println(" _____________________________");
		System.out.println("|                             |");
		System.out.println("| Anykey + enter: Takaisin    |");
		System.out.println("|_____________________________|\r\n");
		READ.next();//tähän jämähtää kunnes käyttäjä syöttää mitätahansa + enter
		p("\n");
		//tässä vaiheessa palataan kutsuvaan metodiin eli valikkoon
	}//ennätykset

	static void kirjoitaTiedosto() throws IOException {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String formatDateTime = now.format(formatter);
		File ennatys = new File("ennätykset.txt");			
		PrintWriter kirjoittaja = new PrintWriter(new FileWriter(ennatys, true));
		kirjoittaja.println(pelaaja + ": " + rahaaEnnatys + "€" + "   " + formatDateTime);
		kirjoittaja.close();
	}

	static void voittaa(int kasi, int panos) {
		System.out.println("Käsi " + kasi + ": voittaa! Voitit " + (panos) + " euroa!");
		rahaa = rahaa + (panos*2);
	}

	static void haviaa(int kasi, int panos) {
		System.out.println("Käsi " + kasi + ": häviää. Hävisit " + panos + " euroa.");
	}

	static void tasapeli(int kasi, int panos) {
		System.out.println("Käsi " + kasi + ": tasapeli, saat rahasi takaisin.");
		rahaa=rahaa+panos;
	}

	static void blackjack(int kasi, int panos) {
		System.out.println("Käsi " + kasi + ": blackjack! Voitit " + (panos * 1.5) + " euroa!");
		rahaa = rahaa + (panos * 2.5);
	}

	static int asetaPanos() {
		System.out.println("Aseta haluamasi panos väliltä 1€-100€: ");
		int panos = lueInt(1, 100);
		return panos;
	}

	static int asetaRahaa() {
		System.out.println("Talleta haluamasi määrä rahaa väliltä 3€-1000€: ");
		int rahaa = lueInt(3, 1000);
		return rahaa;
	}

	static void voittaako(int panos) {
		int j = 1;
		for (int i = 1; i < paikkoja + 1; i++) {
			if (vakuutus[i]) {
				if (paikka[0][1] == 1 && paikka[0][2] == 10) {
					vakuutettuVoittaa(j, panos);
				} else {
					if ((paikka[0][0] > 21 && paikka[i][0] < 22)
							|| (paikka[0][0] < paikka[i][0] && paikka[i][0] < 22)) {
						vakuutettuHaviaa2(j, panos);
					} else {
						vakuutettuHaviaa1(j, panos);
					}
				}
			} else if (tuplaus[i]) {
				if ((paikka[0][0] > 21 && paikka[i][0] < 22) || (paikka[0][0] < paikka[i][0] && paikka[i][0] < 22)) 
{
					tuplausVoittaa(j, panos);
				} else if (paikka[0][0] == paikka[i][0]) {
					tasapeli(j, panos);
				} else {
					tuplausHaviaa(j, panos);
				}
			} else if ((paikka[i][1] == 10 && paikka[i][2] == 1 && paikka[0][1] + paikka[0][2] != 21)
					|| (paikka[i][1] == 1 && paikka[i][2] == 10 && paikka[0][1] + paikka[0][2] != 21)) {
				blackjack(j, panos);
			} else {
				if ((paikka[0][0] > 21 && paikka[i][0] < 22) || (paikka[0][0] < paikka[i][0] && paikka[i][0] < 22)) 
{
					voittaa(j, panos);
				} else if (paikka[0][0] == paikka[i][0] && paikka[i][0]<22) {
					tasapeli(j, panos);
				} else {
					if (i>2 && jaettu[i]==true) {
						haviaa(j, panos);
					} else {	
						haviaa(j, panos);
					}
				}
			}
			j++;
		}
		System.out.println("\r\nSinulla on " + rahaa + " euroa.\r\n");
	}

	static void vakuutettuVoittaa(int kasi, int panos) {
		System.out.println("Käden " + kasi + " vakuutus voittaa! Saat rahasi takaisin.");
		rahaa=rahaa+panos;
	}

	static void vakuutettuHaviaa1(int kasi, int panos) {
		rahaa = rahaa - (panos * 0.5);
		System.out.println("Sekä vakuutus, että käsi " + kasi + ": häviää! Hävisit " + (panos * 1.5) + " euroa!");
	}

	static void vakuutettuHaviaa2(int kasi, int panos) {
		rahaa = rahaa + (panos*1.5);
		System.out.println("Vakuutus hävisi, mutta käsi " + kasi + ": voittaa! Voitit " + (panos / 2) + " euroa!");
	}

	static void tuplausVoittaa(int kasi, int panos) {
		rahaa = rahaa + (panos * 4);
		System.out.println("Tuplattu käsi " + kasi + " voittaa! Voitit " + (panos * 2) + " euroa!");
	}

	static void tuplausHaviaa(int kasi, int panos) {
		System.out.println("Tuplattu käsi " + kasi + " häviää! Hävisit " + (panos * 2) + " euroa!");
	}

	static void sekoitaPakka(int[] arr, char arr2[][]) {
		// arr[] = pelipakan korttien arvot, arr2[][] = pelipakan korttien symbolit ja
		// maat

		int rnd, tmp;
		char symboli, maa;

		for (int i = 0; i < arr.length; i++) {// käydään koko pakka läpi
			rnd = random.nextInt(arr.length); // arvotaan satunnaisluku väliltä 0-51

			// tallennetaan korttiarvo ja symbolit apumuuttujiin
			tmp = arr[rnd];
			symboli = arr2[0][rnd];
			maa = arr2[1][rnd];

			// siirretään kortteja
			arr[rnd] = arr[i];
			arr2[0][rnd] = arr2[0][i];
			arr2[1][rnd] = arr2[1][i];

			arr[i] = tmp;
			arr2[0][i] = symboli;
			arr2[1][i] = maa;

			// koko roska uusiksi 52 kertaa
		} // for
	}// sekoita pakka

	static void jaaKortti(int p) {
		// argumenttiin pelipaikan numero

		// Vakuutus muuttuu laittomaksi kun ensimmäinen kortti on jaettu
		saaVakuuttaa = false;

		// raja 13 koska teoreettinen maksimi kortteja bläkkärissä yhteen käteen
		// 6*A = 6/16 + 6 = 12 + 4*A = 16 + mikätahansa >= 17 | yht 10*A, 1*6 ja yksi
		// satunnainen = 12 kpl
		for (int i = 1; i < 13; i++) {

			if (paikka[p][i] == 0) {// jos paikkaan p ei ole jaettu iidennettä korttia, eli sen arvo == 0

				// jaetaan pakasta uusi kortti
				paikka[p][i] = korttienArvot[korttiNro];
				kuvakkeet[p][i][0] = korttienSymbolit[0][korttiNro];
				kuvakkeet[p][i][1] = korttienSymbolit[1][korttiNro];

				korttiNro++;// globaali muuttuja, vahtii kuinka monta korttia pakasta on otettu
				break;
			} // if paikka tyhjä eli sen arvo on vielä nolla
		} // for - kortin jakaminen

		// kun kortti on jaettu, summataan korttien arvot pelipaikassa p
		paikka[p][0] = summaaKortit(p, true);

	}// jaa kortti

	static int summaaKortit(int p, boolean b) {
		// ensimmäiseen argumenttiin pelipaikan numero
		// toinen argumentti:
		// true = palauttaa pinon todellisen arvon, esim 7+A = 18
		// false = palauttaa arvon joka on kymmenen vähemmän, esim 7+A = 8

		int tmp = 0;

		for (int i = 1; i < 13; i++) {
			tmp = tmp + paikka[p][i];// lisätään korttien arvot summaan

			// jos seuraavassa tarkasteltavassa paikassa ei ole korttia, looppi keskeytetään
			// breakilla
			if (paikka[p][i + 1] == 0)
				break;

		} // for - summaa korttien arvot ja tallentaa ne nolla-indeksiin

		// jos b=true, pakasta löytyy ässä ja yhteenlaskettu summa jäi alle 12 ->
		// lisätään summaan 10.
		if (b && onkoAssa(p) && (tmp < 12))
			tmp = tmp + 10;

		return tmp;

	}// summaa kortit

	static boolean onkoBlackjack(int p) {
		// argumenttiin pelipaikan numero

		// EI SAA OLLA JAETTU KÄSI!!

		if (paikka[p][3] == 0) {// jos ei ole jaettu kolmatta korttia

			// jos kortit 1 ja 2: ovat 1 & 10 tai 10 & 1
			if (paikka[p][1] == 1 && paikka[p][2] == 10)
				return true;
			if (paikka[p][1] == 10 && paikka[p][2] == 1)
				return true;

		} // if - onko jaettu kolmatta korttia
		return false;
	}// onko bläkkäri

	static boolean onkoAssa(int p) {
		// argumenttiin pelipaikan paikkanumero jota tarkastellaan

		for (int i = 0; i < 13; i++) {

			if (paikka[p][i] == 1)
				return true; // jos paikan arvo on 1 niin sehän on ässä -> return true
			if (paikka[p][i + 1] == 0)
				break; // jos seuraavista tarkasteltavista paikoista ei löydy kortteja -> break
		} // for

		return false; // palautetaan false jos ässää ei löytynyt
	}// onko ässä

	static void pelaajanToiminnot() { 
		//melkein koko metodi on saman while loopin sisällä 
		//alussa on poikkeustapauksia 
		//jos päästään switch caseen asti kysytään pelaajan toimintoa 

		//while looppia suoritetaan niin pitkään kunnes on jakajan vuoro, eli kun vuoro == 0 
		while (vuoro>0) { 

			pelaajanSiirrot();

			//jos pelikädessä ei ole toista korttia ja käsi on jaettu 
			//saa automaattisesti uuden kortin 
			if (paikka[vuoro][2]==0 && jaettu[vuoro]) { 
				jaaKortti(vuoro); 
				delay = 1000;
				printtaa(); 
				continue; 
			}//if - jaettu yksikorttinen 

			//jos pelipaikassa on blackjack (ei koske jaettua kättä) menettää vuoronsa automaattiseti 
			if (onkoBlackjack(vuoro) && !jaettu[vuoro]) { 
				delay = 1000;
				vuoro--; 
				printtaa();
				continue;
			}//if - paikassa bläkkäri 

			//jos viimeinen pelaajan käsi oli bläkkäri breakataan looppi 
			if (vuoro<1) break; 

			//switch casea toistetaan siihen asti kunnes kaikkien pelaajien vuoro on pelattu 
			switch (lueInt(1,5)) { 
			//(lueInt(1,5): Kysytään käyttäjältä kokonaislukuarvoa väliltä 1-5 


			//lisää 
			case 1: 
				jaaKortti(vuoro); //jaetaan kortti 
				if (onkoYli(vuoro)) vuoro--; //jos meni yli, vuoro automaattisesti seuraavalle 
				break; 

				//jää 
			case 2: 
				vuoro--; //vuoro siirtyy seuraavalle 
				break; 

				//tuplaa 
			case 3: 
				if (voikoTuplata(vuoro)) {//jos käden pystyy tuplaamaan 
					tuplaaPanos(tuplaus);
					rahaa=rahaa-panos;
					delay = 1200;
					jaaKortti(vuoro); //jaetaan kortti 
					vuoro--; //tuplauksen jälkeen saa vain yhden kortin eli vuoro päättyy samantien 
				}//if - pelikäden pystyy tuplaamaan 
				break; //break suoritetaan siitä huolimatta oliko voikoTuplata true tai false 

				//jaa 
			case 4: 
				if (voikoJakaa(vuoro)) {//jos käden pystyy jakamaan 
					rahaa=rahaa-panos;
					jako(vuoro); //jaetaan käsi 
					delay = 1000;//1500 millisekunnin viive seuraavaan printtiin
					printtaa();
					jaaKortti(vuoro); //jaetaan kortti. Jaon jälkeen on pakko ottaa vähintään yksi kortti molempiin 
					delay = 1000;

					//jos jaettu käsi oli ässäpari 
					//vuoro ohi, uusi kortti toisen ässän päälle ja vuoro taas ohi 
					if (paikka[vuoro][1]==1) { 
						printtaa(); 
						vuoro--; 
						delay = 700;
						jaaKortti(vuoro); 
						vuoro--; 
					}//if - oli ässäpari 
				}//if - pelikäden pystyy jakamaan 
				break; //break suoritetaan siitä huolimatta oliko voikoJakaa true tai false 

				//vakuutus 
			case 5: 
				if (saaVakuuttaa) vakuuttaako(vakuutus); 
				break; 

				//vakuuta 
			}//switch 

			printtaa(); //printataan pelitilanne ruudulle kun pelaaja on tehnyt valinnan 


		}//while - jos ei ole vielä jakajan vuoro -> takaisin alkuun 

	}//pelaajan toiminnot 

	static void jakajanToiminnot () { 
		//jakaja ei tee muuta kuin ottaa niin kauvan korttia että saa vähintään 17 
		while ((paikka[0][0]<17)) {
			jaaKortti(0); 
			delay = 1000;//sekunnin lisäviive
			printtaa(); 
		}
		peliKaynnissa = false;

	}//jakajan toiminnot 

	static boolean onkoYli(int p) {
		// argumenttiin pelipaikan numero

		if (paikka[p][0] > 21)
			return true;
		else
			return false;
	}// onko yli



	static void perusjako() { 
		//tekee perusjaon ja tarkistaa vakuutuksen laillisuuden 

		for (int i = paikkoja; i>-1;i--) { 
			//jaetaan ensimmäiset kortit 
			jaaKortti(i); 
			printtaa();
		}//for 

		for (int i = paikkoja; i>0;i--) { 
			//jaetaan toiset kortit (ei paikkaan 0) 
			jaaKortti(i); 
			printtaa();
		}//for 

		//jos jakajalla ässä 
		if (paikka[0][1]==1) saaVakuuttaa = true; 
		else saaVakuuttaa = false; 

	}//perusjako 


	static void pelaajanSiirrot() {
		//pelipaikan lailliset siirrot kyseiselle pelipaikalle

		//Tulostetaan pelaajan lailliset siirrot 
		p("1. Lisää, 2. Jää"); 
		if (voikoTuplata(vuoro)) p(", 3. Tuplaus"); 
		//jos tuplaus on laillinen siirto tulostetaan ruudulle tuplaus
		if (voikoJakaa(vuoro)) p(", 4. Jako"); 
		//jos jakaminen on laillinen siirto tulostetaan ruudulle jako
		if (saaVakuuttaa) p(", 5. Vakuutus"); 
		//jos vakuutus on laillinen siirto tulostetaan ruudulle vakuutus
		p("\n");
	}//pelaajanSiirrot



	static void printtaa() { 

		p("\n\n\n\n\n\n\n\n\n"); 

		//debug();//vikojen hakuun 

		grafiikka();//Tulostaa kortit, pelitilanteet ja himmelit 

		p("\n"); 

		odota();

	}//printtaa 

	static void odota() {

		int tmp = 600;//vakioviive

		if (delay>1) {
			tmp = tmp+delay;
			delay = 1;
		}//if delay annettu

		//jos delayksi asetetaan nolla (tai negatiivinen) ei tule viivettä;
		if (delay<0) {
			tmp = 0;
			delay = 1;
		}

		if (tmp>0) try {
			TimeUnit.MILLISECONDS.sleep(tmp);
		}
		catch (Exception e) {
			p("error: delay");
		}
	}

	static void debug() {// vikojen hakuun

		p("paikkoja: " + paikkoja + "\t(alussa " + 3 + ")");
		p("\nvuoro: " + vuoro + "\t korttiNro:" + korttiNro);
		p("\nSeuraavat kolme korttia: " + korttienArvot[korttiNro] + ", " + korttienArvot[korttiNro + 1] + ", "
				+ korttienArvot[korttiNro + 2]);

		p("\njaettutaulu (" + jaettu.length + "):");
		for (int i = 0; i < jaettu.length; i++) {
			p(" " + i + ":");
			if (jaettu[i])
				p("1");
			else
				p("0");

		} // for

		p("\ntuplaustaulu (" + tuplaus.length + "):");
		for (int i = 0; i < tuplaus.length; i++) {
			p(" " + i + ":");
			if (tuplaus[i])
				p("1");
			else
				p("0");
		} // for

		p("\nvakuutustaulu (" + vakuutus.length + "):");
		for (int i = 0; i < vakuutus.length; i++) {
			p(" " + i + ":");
			if (vakuutus[i])
				p("1");
			else
				p("0");
		} // for

		p("\n");
	}// debug

	static void p(String str) {// System.out.print(" ") -> p(" ")
		System.out.print(str);
	}// p

	static void grafiikka() {

		jakajanKortit();// printtaa jakajan kortit
		peliPisteet();// printtaa korttien väliin pelaajan pisteet ja panokset ym.
		pelaajanKortit();// printtaa pelaajan kaikki korttipinot

	}

	static void kortti(int rivi, char[] k) {
		// printtaa pelikortista yhden rivin

		// ensimmäinen argumentti: Rivinumero joka kortista halutaan printata
		// toinen argumentti: Printattavan kortin symboli ja maa, tallennetaan
		// apumuuttujiin
		char s = k[0];// symboli
		char m = k[1];// maa

		// switch casella valikoidaan printattava rivi argumentin perusteella
		switch (rivi) {

		case 0:
			p(" _______ ");
			break;
		case 1:
			p("|" + s + "      |");
			break;
		case 2:
			p("|       |");
			break;
		case 3:
			p("|   " + m + "   |");
			break;
		case 4:
			p("|       |");
			break;
		case 5:
			p("|______" + s + "|");
			break;
		default:
			p("         ");
			break;
		}// switch

	}// kortti

	static void jakajanKortit() {// talon korttien printtaus
		p("\n");

		// TALON KOKONAISPISTEET
		p("Talo: ");

		// jos paikassa on bläkkäri printataan tekstinä
		if (onkoBlackjack(0))
			p("BJ");

		else {
			// jos pelipaikassa on ässä,
			// ja korttien arvo on alle 11 (ässät ykkösiä)
			// ja korttien arvo on alle 17 (todellinen käden arvo)
			// printataan myös se pienempi korttipinon arvo ja viiva väliin
			if (onkoAssa(0) && (summaaKortit(0, false) < 12) && (summaaKortit(0, true) < 17))
				p(summaaKortit(0, false) + "/");
			else
				p(" ");

			// printtaa paikan pisteet
			p("" + paikka[0][0]);

		} // else ei bläkkäriä

		if (vuoro == 0)
			p(" <--"); // jos on jakajan vuoro printataan nuoli
		p("\n\n");

		// looppi tulostaa jakajan kaikki kortit vierekkäin yhteensä kuudelle riville
		for (int korttirivi = 0; korttirivi < 6; korttirivi++) {

			// yksi rivi. Toistojen määrä riippuu siitä kuinka monta korttia jakajalla on
			for (int i = 1; i <= korttejaPaikassa(0); i++) {
				p(" ");
				kortti(korttirivi, kuvakkeet[0][i]);// kortin rivi i/5
				p(" ");
			} // for - kortin yläreuna

			p("\n");

		} // korttirivit- toistetaan yhtä monta kertaa kun kortissa on rivejä (6)

		p("\n\n");

	}// jakajanKortit- talon korttien printtaus

	static void peliPisteet() { 

		int k;//ensimmäisen rivin kolmannen loopin apumuuttuja
		int c;//toisen rivin apumuuttuja mikä laskee merkkimääriä

		//kaksi looppia: molemmat looppaa yhtä monta kertaa kun on pelipaikkoja 

		//printtaa paikkojen nimet
		for (int i = 1; i<paikka.length;i++) {
			p(""+paikanNimi[i]);

			//tulostaa sopivan määrän välilyöntiä
			for (int j = paikanNimi[i].length();j<=10;j++) {
				p(" ");
			}//for lisää tyhjää

			if (vuoro==i) {
				p("<--");
				k=3;
			}
			else k=0;

			//6-9 välilyöntiä ennen seuraavaa nimeä 
			for (;k<9;k++) {
				p(" ");		
			}//for välilyöntejä

		}//for nimet


		p("\n");


		//printtaa pisteet ja panokset
		for (int i = 1; i<paikka.length;i++) { 

			c=0;

			//printtaa vakuutetun käden merkiksi tähden pisteiden oikealle puolelle 
			p(" "); 
			if(vakuutus[i]) {
				p("*"); 
				c=c+1;
			}

			//jos paikassa on bläkkäri (ei jaetulle kädelle) printataan tekstinä 
			if(onkoBlackjack(i) && !jaettu[i]) {
				p("BJ"); 
				c=c+2;
			}//if jos bläkkäri

			else { 

				//jos pelipaikassa on ässä, 
				//ja vuoro on suurempi tai yhtäsuuri kuin kyseinen pelipaikka, 
				//ja korttien arvo on alle 12, 
				//printataan myös se pienempi korttipinon arvo 
				if (onkoAssa(i) && vuoro>=i && (summaaKortit(i, false)<12)) {
					p(summaaKortit(i,false)+"/"); 
					if (summaaKortit(i,false)>9) c=c+3;
					else c=c+2;
				}//if - printataanko kaksi arvoa


				//printtaa paikan pisteet 
				p(""+paikka[i][0]);
				if (paikka[i][0]>9) c=c+2;
				else c=c+1;

			}//else ei bläkkäriä 

			//välilyöntejä
			for (;c<9;c++) {
				p(" ");
			}//for välilyöntejä
 

			for (int j = 0; j<10;j++) {
				p(" ");
			}
		} 
		p("\n\n"); 

	}//peliPisteet



	static void pelaajanKortit() { 



		//muuttuja laskee kuinka monta viivaa korttipinon vasemmalle puolelle on printattu 
		//luku vähennetään siitä määrästä välilyöntejä mikä korttipinojen väliin tulee 
		int viivoja; 



		//korttien yläreuna 
		for (int i = 1; i<paikka.length;i++) { 
			p(" "); 
			if (korttejaPaikassa(i)>0) kortti(0, kuvakkeet[i][0]);//rivi 0 = yläreuna 
			else kortti(6, kuvakkeet[i][0]);
			p("          "); 
		} 
		p("\n"); 


		/* 

  rivilooppi: Printtaa koko rivin. Alkaa rivin alusta ja loppuu rivin loppuun. 
  pelipaikka: Toistetaan yhtä monta kertaa kun on pelipaikkoja. 

  for ( rivi ) { 
  for ( pelipaikka ) = { 

  ( korttipinoja n kappaletta ) 

  }//for pelipaikka 
  }//for rivi 
		 */ 
		for (int rivi = 1;rivi<(10);rivi++) {
			p(" "); 

			//riviltä 6 eteenpäin printataan rivin alkuun ylimääräisiä välilyöntejä 
			for (int i = rivi;i>5;i--) { 
				p(" "); 
			}//for - välilyöntejä rivin alkuun 


			//pelipaikan kortti. Toistetaan yhtä monta kertaa kun on pelipaikkoja (3) 
			for (int pelipaikka = 1;pelipaikka<paikka.length;pelipaikka++) { 


				//Laskee pystyviivat korttipinon vasemmalle puolelle 
				viivoja = 1; 

				//pystyviivojen printtauslooppi 
				for (int i = 1;i<=rivi ;i++) { 

					//ensimmäinen ehto = jos pelipaikasta löytyy iidennes+1 kortti 
					if (paikka[pelipaikka][i+1]>0 && rivi>i && rivi<5+i) { 
						p("|"); 
						viivoja++; 
					}//if 
				}//for - pystyviivat 

				//loput kortista 
				if (korttejaPaikassa(pelipaikka)>0){

					if (paikka[pelipaikka][rivi]>0) { //jos rivillä on uusi kortti 


						//jos seuraavalle riville tulee myös kortti -> tehdään sille yläreuna 
						if (paikka[pelipaikka][rivi+1]>0) { 

							p("|"+kuvakkeet[pelipaikka][rivi][0]); 
							p("______|"); 
						} 
						//jos ei tule printataan pelkän kortin rivi numero 1 
						else kortti (1,kuvakkeet[pelipaikka][rivi]); 
					} 

					else if (paikka[pelipaikka][rivi-1]>0) kortti(2,kuvakkeet[pelipaikka][rivi-1]); 
					else if (paikka[pelipaikka][rivi-2]>0) kortti(3,kuvakkeet[pelipaikka][rivi-2]); 
					else if (paikka[pelipaikka][rivi-3]>0) kortti(4,kuvakkeet[pelipaikka][rivi-3]); 
					else if (paikka[pelipaikka][rivi-4]>0) kortti(5,kuvakkeet[pelipaikka][rivi-4]); 
					else kortti(6,kuvakkeet[pelipaikka][rivi-5]); 
				} else kortti(6,kuvakkeet[pelipaikka][0]);

				//tyhjät välit ennen seuraavaa korttipinoa 
				for (int i = 0; i<(12-viivoja);i++) { 
					p(" "); 
				}//for - tyhjät välit 

			}//for - korttipinot 

			p("\n");  

		}//for - rivi 



	}//pelaajanKortit

	static int korttejaPaikassa(int p) {
		// argumenttiin pelipaikan numero

		for (int i = 0; i < 13; i++) {
			if ((paikka[p][i + 1]) == 0)
				return i;
			// jos seuraava indeksi on tyhjä palauttaa nykyisen indeksin arvon
		}

		return 10101010;// tähän ei pitäisi päästä ikinä
	}// kortteja paikassa

	// LIENEE TÄLLÄ HETKELLÄ TURHA. GRAFIIKAT KORVAAVAT TULOSTETTAVAT TAULUKOT
	static void printtaaTaulukko(int p, int[][] arr) {
		for (int i = 0; i < arr[p].length; i++) {
			p(arr[p][i] + " ");
			if (arr[p][i + 1] == 0)
				break;
		} // for
	}// printtaaTaulukko

	static int lueInt(int min, int max) {
		// luetaan käyttäjän syöte
		// pitää olla lukuarvo (-> while looppi) ja välillä min-max (-> arvot
		// argumentista & if lause)
		int tmp; // tähän tallennetaan käyttäjän syöte KUN se on kelvollinen lukuarvo
		boolean b = false; // tämä trueksi kun käyttäjän syöte on kelvollinen

		do {
			while (!READ.hasNextInt()) {
				p("Antamasi merkki ei kelpaa, anna uusi luku:");
				READ.next();
			} // while ei kokonaisluku

			tmp = READ.nextInt();

			if (tmp > min - 1 && tmp < max + 1)
				b = true; // osuuko syöte min ja max väliin
			else {
				System.out.println("Antamasi merkki ei kelpaa, anna uusi luku:");
			}
		} while (!b);

		return tmp;
	}// lueInt



	static void lisaaPaikka(int p) {
		//lisää korttipaikan esim. jakamista varten


		//uudet taulukot joissa on yksi rivi enemmän kun wanhoissa tauluissa 
		int[][] tempArr = new int [paikka.length+1][13]; 
		char[][][] tempSymbolArr = new char [paikka.length+1][13][2]; 
		boolean[] tmpJako = new boolean [paikka.length+1]; 
		boolean[] tmpTuplaus = new boolean [paikka.length+1]; 
		boolean[] tmpVakuutus = new boolean [paikka.length+1]; 
		String[] tmpNimi = new String [paikka.length+1];

		//kopioidaan rivit uuteen taulukkoon siten, että jaetun paikan viereen tulee tyhjä rivi (-> tapaus i == p) 
		for (int i = 0; i<paikka.length;i++) { 
			if (i<p) { 
				tempArr[i] = paikka[i]; 
				tempSymbolArr[i] = kuvakkeet[i]; 
				tmpJako[i] = jaettu[i]; 
				tmpTuplaus[i] = tuplaus[i]; 
				tmpVakuutus[i] = vakuutus[i]; 
				tmpNimi[i] = paikanNimi[i];
				continue; 

			}//jos indeksi on pienempi kuin paikan lukuarvo kopioidaan suoraan 

			if (i>p) { 
				tempArr[i+1] = paikka[i]; 
				tempSymbolArr[i+1] = kuvakkeet[i]; 
				tmpJako[i+1] = jaettu[i]; 
				tmpTuplaus[i+1] = tuplaus[i]; 
				tmpVakuutus[i+1] = vakuutus[i]; 
				tmpNimi[i+1] = paikanNimi[i];
			}//jos indeksi on suurempi kuin paikan lukuarvo kopioidaan yksi eteenpäin 

		}//for -kopioi rivit 

		//kopioidaan paikan nimi tyhjiin
		//jos kutsutaan menusta viimeiseen riviin: tulee OOB ilman if elseä
		if (paikanNimi.length==p) {
			tmpNimi[p] = ("Pelaaja "+p); 
		} 
		else {
			tmpNimi[p] = paikanNimi[p]; 
			tmpNimi[p+1] = paikanNimi[p];
		}//else

		if (peliKaynnissa) {
			//siirretään kortit yhdestä kädestä kahteen 
			tempArr[p][1] = paikka[p][1]; 
			tempArr[p+1][1] = paikka[p][2]; 
			tempSymbolArr[p][1] = kuvakkeet[p][1]; 
			tempSymbolArr[p+1][1] = kuvakkeet[p][2];	

			//päivitetään summa 
			tempArr[p][0] = paikka[p][1]; 
			tempArr[p+1][0] = paikka[p][2]; 

			vuoro++; //lisätään vuoronumeroa koska käsiä on nyt yksi enemmän 

		}//jos peli on käynnissä siirretään myös kortit ja päivitetään summat

		//kopioidaan muokatut taulukot vanhojen päälle
		paikka = tempArr; 
		kuvakkeet = tempSymbolArr; 
		jaettu = tmpJako; 
		tuplaus = tmpTuplaus; 
		vakuutus = tmpVakuutus; 
		paikanNimi = tmpNimi;

		paikkoja++;

	}//lisää paikka

	static void poistaPaikka(int p) {
		//argumenttiin paikkanumero
		//käytetään pelipaikka valikossa

		int[][] tempArr = new int [paikka.length-1][13];
		String[] tmpNimi = new String [paikka.length-1];

		//kopioidaan rivit uuteen taulukkoon siten, että jaetun paikan viereen tulee tyhjä rivi (-> tapaus i == p) 
		for (int i = 0; i<paikka.length;i++) { 
			if (i<p) { 
				tempArr[i] = paikka[i];
				tmpNimi[i] = paikanNimi[i];
				continue; 

			}//jos indeksi on pienempi kuin paikan lukuarvo kopioidaan suoraan 

			if (i>p) { 
				tempArr[i-1] = paikka[i];
				tmpNimi[i-1] = paikanNimi[i];
			}//jos indeksi on suurempi kuin paikan lukuarvo kopioidaan yksi alaspäin 

		}//for -kopioi rivit 

		//kopioidaan muokatut taulukot vanhojen päälle
		paikka = tempArr; 
		paikanNimi = tmpNimi;

	}//poistapaikka



	private static void jako(int p) { 
		//korttien jakamismetodi 

		lisaaPaikka(p);


		//tallennetaan jaetun paikan paikkanumero 
		jaettu[vuoro-1] = true; 
		jaettu[vuoro] = true; 

	}//jako 



	static void vakuuttaako(boolean[] v) { // kysyy käyttäjältä käsien vakuutuksesta
		int valinta = 0;
		for (int i = 1; i < v.length; i++) {
			if (rahaa - panos >= 0) {
				System.out.println("Haluatko vakuuttaa käden " + (i) + "? Kyllä = 1, Ei = 2.");
				valinta = lueInt(1, 2);
			} else {
				System.out.println("Sinulla ei ole tarpeeksi rahaa muiden käsien vakuuttamiseen.");
				break;
			}

			if (valinta == 1) {
				rahaa=rahaa-panos;
				vakuutus[i] = true;
			}
		}
		saaVakuuttaa = false;
	}// vakuttaako

	static void tuplaaPanos(boolean[] t) { // kysyy käyttäjältä käsien vakuutuksesta
		tuplaus[vuoro] = true;
	}// tuplaaPanos

	static boolean voikoJakaa(int p) {
		// argumenttiin pelikäden paikkanumero

		// jaettua kättä ei voi jakaa
		if (jaettu[p])
			return false;

		// vakuutettua kättä ei voi jakaa
		if (vakuutus[p])
			return false;

		// jos rahaa liian vähän jakamiseen
		if (rahaa - panos < 0)
			return false;

		// jos kolmatta korttia ei ole jaettu ja ensimmäinen ja toinen kortti on sama
		if (paikka[p][3] == 0 && paikka[p][1] == paikka[p][2])
			return true;

		else
			return false;

	}// voiko jakaa

	static boolean voikoTuplata(int p) {
		// argumenttiin pelikäden paikkanumero

		// jos rahaa liian vähän tuplaamiseen
		if (rahaa - panos < 0)
			return false;

		// vakuutettua kättä ei voi tuplata
		if (vakuutus[p])
			return false;

		// jos kolmatta korttia ei jaettu, ensimmäisen ja toisen kortin summa on yli 8
		// ja alle 12.
		// tämä sääntö on ainakin joskus ollut käytössä suomessa, eli tuplata saa
		// 9,10,11
		if (paikka[p][3] == 0 && (paikka[p][1] + paikka[p][2]) > 8 && (paikka[p][1] + paikka[p][2]) < 12) {
			return true;
		} // if

		else
			return false;

	}// voikoTuplata

}//class
