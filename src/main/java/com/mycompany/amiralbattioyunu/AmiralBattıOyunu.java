/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.amiralbattioyunu;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class AmiralBattıOyunu extends JFrame {
    private int BOYUT = 6;
    private int kalanAtışHakkı = 48;

   
    private final int BOŞ = 0;
    private final int GEMİ = 1;
    private final int VURULDU = 2;
    private final int ISKALANDI = 3;

    private int[][] kullanıcıTahtası; 
    private int[][] bilgisayarTahtası; 

    private JButton[][] kullanıcıDüğmeleri; 
    private JButton[][] bilgisayarDüğmeleri; 

    private JPanel kullanıcıPaneli; 
    private JPanel bilgisayarPaneli; 
    private JPanel kontrolPaneli; 

    private JButton başlatDüğmesi; 
    private JButton otomatikYerleştirDüğmesi; 
    private JButton sıfırlaDüğmesi; 
    private JButton yönDeğiştirDüğmesi; 

    private JComboBox<String> zorlukSeçici; 
    private JLabel manuelBilgiEtiketi; 
    
    private boolean manuelYerleştirmeModu = true; 
    private int manuelGemiSırası = 0; 
    private boolean manuelGemiDikeyMi = false; 

    private final int[] gemiBoyutları = {2, 2, 2, 3, 4}; 

    private Random rastgele = new Random(); 

    private boolean kullanıcıSırası = true; 

    
    private Icon ikon2Yatay,
                   ikon2Dikey,
                   ikon3Yatay,
                   ikon3Dikey,
                   ikon4Yatay,
                   ikon4Dikey;

    public AmiralBattıOyunu() {
        setTitle("Amiral Battı - Kalan tur: 48 (Manuel yerleştirme)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        kontrolPaneli = new JPanel();

        zorlukSeçici = new JComboBox<>(
                new String[]{
                        "Kolay (6x6 - 24 Atış)",
                        "Orta (8x8 - 50 Atış)",
                        "Zor (10x10 - 80 Atış)"
                });
        kontrolPaneli.add(new JLabel("Zorluk Seçimi:"));
        kontrolPaneli.add(zorlukSeçici);

        otomatikYerleştirDüğmesi = new JButton("Otomatik Gemi Yerleştir");
        kontrolPaneli.add(otomatikYerleştirDüğmesi);

        yönDeğiştirDüğmesi = new JButton("Yönü Değiştir (Şu an: Yatay)");
        kontrolPaneli.add(yönDeğiştirDüğmesi);

        başlatDüğmesi = new JButton("Oyunu Başlat");
        kontrolPaneli.add(başlatDüğmesi);

        sıfırlaDüğmesi = new JButton("Sıfırla");
        kontrolPaneli.add(sıfırlaDüğmesi);

        manuelBilgiEtiketi = new JLabel("Manuel yerleştirme: 1. gemi (2 birim) için yer seçin.");
        kontrolPaneli.add(manuelBilgiEtiketi);

        add(kontrolPaneli, BorderLayout.NORTH);

        kullanıcıPaneli = new JPanel();
        bilgisayarPaneli = new JPanel();

        add(kullanıcıPaneli, BorderLayout.WEST);
        add(bilgisayarPaneli, BorderLayout.EAST);

        ikonlarıHazırla(); 

        zorlukAyarlama(0); 
        tahtalarıBaşlat(); 

        zorlukSeçici.addActionListener(e -> {
            int seçim = zorlukSeçici.getSelectedIndex();
            zorlukAyarlama(seçim);
            tahtalarıBaşlat();
            manuelYerleştirmeModu = true;
            manuelGemiSırası = 0;
            manuelGemiDikeyMi = false;
            yönDeğiştirDüğmesi.setText("Yönü Değiştir (Şu an: Yatay)");
            manuelBilgiEtiketi.setText("Manuel yerleştirme: 1. gemi (2 birim) için yer seçin.");
            bilgisayarDüğmeleriniEtkinleştir(false); 
            kullanıcıDüğmeleriniEtkinleştir(true); 
        });

        otomatikYerleştirDüğmesi.addActionListener(e -> {
            manuelYerleştirmeModu = false;
            gemileriRastgeleYerleştir(kullanıcıTahtası, kullanıcıDüğmeleri, true); 
            manuelGemiSırası = gemiBoyutları.length;
            manuelBilgiEtiketi.setText("Otomatik yerleştirme tamamlandı.");
        });

        yönDeğiştirDüğmesi.addActionListener(e -> {
            manuelGemiDikeyMi = !manuelGemiDikeyMi;
            yönDeğiştirDüğmesi.setText("Yönü Değiştir (Şu an: " + (manuelGemiDikeyMi ? "Dikey" : "Yatay") + ")");
        });

        başlatDüğmesi.addActionListener(e -> {
            if (manuelYerleştirmeModu && manuelGemiSırası < gemiBoyutları.length) {
                JOptionPane.showMessageDialog(this, "Tüm gemileri yerleştirmeden oyuna başlayamazsınız!");
                return;
            }
            if (!manuelYerleştirmeModu && manuelGemiSırası < gemiBoyutları.length) {
                gemileriRastgeleYerleştir(kullanıcıTahtası, kullanıcıDüğmeleri, true);
            }
            gemileriRastgeleYerleştir(bilgisayarTahtası, bilgisayarDüğmeleri, false);

            kullanıcıSırası = true;
            başlığıAtışSayısıİleGüncelle();
            bilgisayarDüğmeleriniEtkinleştir(true);
            kullanıcıDüğmeleriniEtkinleştir(false);
            JOptionPane.showMessageDialog(this, "Oyun başladı! Bilgisayarın tahtasına atış yapabilirsiniz.");
        });

        sıfırlaDüğmesi.addActionListener(e -> {
            tahtalarıBaşlat();
            manuelYerleştirmeModu = true;
            manuelGemiSırası = 0;
            manuelGemiDikeyMi = false;
            yönDeğiştirDüğmesi.setText("Yönü Değiştir (Şu an: Yatay)");
            manuelBilgiEtiketi.setText("Manuel yerleştirme: 1. gemi (2 birim) için yer seçin.");
            kullanıcıSırası = true;
            başlığıAtışSayısıİleGüncelle();
            bilgisayarDüğmeleriniEtkinleştir(false);
            kullanıcıDüğmeleriniEtkinleştir(true);
        });

        setSize(1500, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void zorlukAyarlama(int seviye) {
        switch (seviye) {
            case 0 -> {
                BOYUT = 6;
                kalanAtışHakkı = 48;
            }
            case 1 -> {
                BOYUT = 8;
                kalanAtışHakkı = 100;
            }
            case 2 -> {
                BOYUT = 10;
                kalanAtışHakkı = 180;
            }
        }
    }

    private void tahtalarıBaşlat() { 
        kullanıcıTahtası = new int[BOYUT][BOYUT];
        bilgisayarTahtası = new int[BOYUT][BOYUT];
        kullanıcıDüğmeleri = new JButton[BOYUT][BOYUT];
        bilgisayarDüğmeleri = new JButton[BOYUT][BOYUT];

        kullanıcıPaneli.removeAll();
        bilgisayarPaneli.removeAll();

        kullanıcıPaneli.setLayout(new GridLayout(BOYUT, BOYUT));
        bilgisayarPaneli.setLayout(new GridLayout(BOYUT, BOYUT));

        kullanıcıDüğmeleriniOluştur(); 
        bilgisayarDüğmeleriniOluştur(); 
        
        kullanıcıPaneli.revalidate();
        kullanıcıPaneli.repaint();
        bilgisayarPaneli.revalidate();
        bilgisayarPaneli.repaint();

        manuelGemiSırası = 0;
        manuelYerleştirmeModu = true;
        bilgisayarDüğmeleriniEtkinleştir(false);
        kullanıcıDüğmeleriniEtkinleştir(true);

        başlığıAtışSayısıİleGüncelle();
    }

    private void kullanıcıDüğmeleriniOluştur() { 
        for (int i = 0; i < BOYUT; i++) {
            for (int j = 0; j < BOYUT; j++) {
                JButton düğme = new JButton();
                düğme.setBackground(Color.CYAN);
                düğme.setMargin(new Insets(0, 0, 0, 0));
                düğme.setFocusPainted(false);

                int satır = i, sütun = j;
                düğme.addActionListener(e -> {
                    if (manuelYerleştirmeModu) {
                        gemiyiManuelYerleştir(satır, sütun); 
                    }
                });

                kullanıcıDüğmeleri[i][j] = düğme;
                kullanıcıPaneli.add(düğme);
            }
        }
    }

    private void bilgisayarDüğmeleriniOluştur() { 
        for (int i = 0; i < BOYUT; i++) {
            for (int j = 0; j < BOYUT; j++) {
                JButton düğme = new JButton();
                düğme.setBackground(Color.LIGHT_GRAY);
                düğme.setMargin(new Insets(0, 0, 0, 0));
                düğme.setFocusPainted(false);

                int satır = i, sütun = j;
                düğme.addActionListener(e -> {
                    if (kullanıcıSırası && kalanAtışHakkı > 0)
                        kullanıcıAtışYap(satır, sütun); 
                });

                bilgisayarDüğmeleri[i][j] = düğme;
                bilgisayarPaneli.add(düğme);
            }
        }
    }

    private void gemiyiManuelYerleştir(int r, int c) { 
        if (manuelGemiSırası >= gemiBoyutları.length) {
            JOptionPane.showMessageDialog(this, "Tüm gemiler yerleştirildi.");
            manuelYerleştirmeModu = false;
            manuelBilgiEtiketi.setText("Otomatik yerleştirme tamamlandı.");
            return;
        }

        int uzunluk = gemiBoyutları[manuelGemiSırası];

        if (!gemiYerleştirilebilirMi(kullanıcıTahtası, r, c, uzunluk, manuelGemiDikeyMi)) { 
            JOptionPane.showMessageDialog(this, "Buraya gemi yerleştirilemez!");
            return;
        }

        gemiyiYerleştir(kullanıcıTahtası, kullanıcıDüğmeleri, r, c, uzunluk, manuelGemiDikeyMi, true); 
        manuelGemiSırası++;
        if (manuelGemiSırası < gemiBoyutları.length) {
            manuelBilgiEtiketi.setText("Manuel yerleştirme: " + (manuelGemiSırası + 1) + ". gemi (" + gemiBoyutları[manuelGemiSırası] + " birim) için yer seçin.");
        } else {
            manuelBilgiEtiketi.setText("Tüm gemiler yerleştirildi.");
            manuelYerleştirmeModu = false;
        }
    }

    private boolean gemiYerleştirilebilirMi(int[][] tahta, int r, int c, int uzunluk, boolean dikey) { 
        if (dikey) {
            if (r + uzunluk > BOYUT) return false;
            for (int i = 0; i < uzunluk; i++) {
                if (tahta[r + i][c] != BOŞ) return false;
            }
        } else {
            if (c + uzunluk > BOYUT) return false;
            for (int i = 0; i < uzunluk; i++) {
                if (tahta[r][c + i] != BOŞ) return false;
            }
        }
        return true;
    }

    private void gemiyiYerleştir(int[][] tahta, JButton[][] düğmeler, int r, int c, int uzunluk, boolean dikey, boolean kullanıcıGemisiMi) { 
        Icon ikon = ikonAl(uzunluk, dikey); 
        for (int i = 0; i < uzunluk; i++) {
            if (dikey) {
                tahta[r + i][c] = GEMİ;
                if (kullanıcıGemisiMi) {
                    düğmeler[r + i][c].setIcon(ikon);
                }
            } else {
                tahta[r][c + i] = GEMİ;
                if (kullanıcıGemisiMi) {
                    düğmeler[r][c + i].setIcon(ikon);
                }
            }
        }
    }

    private Icon ikonAl(int uzunluk, boolean dikey) { 
        return switch (uzunluk) {
            case 2 -> dikey ? ikon2Dikey : ikon2Yatay;
            case 3 -> dikey ? ikon3Dikey : ikon3Yatay;
            case 4 -> dikey ? ikon4Dikey : ikon4Yatay;
            default -> null;
        };
    }

    private void gemileriRastgeleYerleştir(int[][] tahta, JButton[][] düğmeler, boolean kullanıcıGemisiMi) {
        
        for (int i = 0; i < BOYUT; i++) {
            for (int j = 0; j < BOYUT; j++) {
                tahta[i][j] = BOŞ;
                if (kullanıcıGemisiMi) {
                    düğmeler[i][j].setIcon(null);
                    düğmeler[i][j].setBackground(Color.CYAN);
                } else {
                    düğmeler[i][j].setBackground(Color.LIGHT_GRAY);
                    düğmeler[i][j].setText("");
                }
            }
        }

        for (int uzunluk : gemiBoyutları) {
            boolean yerleştirildi = false;
            while (!yerleştirildi) {
                int r = rastgele.nextInt(BOYUT);
                int c = rastgele.nextInt(BOYUT);
                boolean dikey = rastgele.nextBoolean();
                if (gemiYerleştirilebilirMi(tahta, r, c, uzunluk, dikey)) {
                    gemiyiYerleştir(tahta, düğmeler, r, c, uzunluk, dikey, kullanıcıGemisiMi);
                    yerleştirildi = true;
                }
            }
        }
    }

    private void kullanıcıAtışYap(int r, int c) { 
       
        if (bilgisayarTahtası[r][c] == VURULDU || bilgisayarTahtası[r][c] == ISKALANDI) return;

        if (bilgisayarTahtası[r][c] == GEMİ) {
            bilgisayarTahtası[r][c] = VURULDU;
            bilgisayarDüğmeleri[r][c].setBackground(Color.RED);
            bilgisayarDüğmeleri[r][c].setText("X");
            başlığıAtışSayısıİleGüncelle();
            if (galibiyetKontrolü(bilgisayarTahtası)) {
                JOptionPane.showMessageDialog(this, "Tebrikler! Oyunu kazandınız.🏆");
                tümDüğmeleriDevreDışıBırak(); 
                return;
            }
           
        } else {
            bilgisayarTahtası[r][c] = ISKALANDI;
            bilgisayarDüğmeleri[r][c].setBackground(Color.BLUE);
            bilgisayarDüğmeleri[r][c].setText(".");
            kullanıcıSırası = false;
            bilgisayarDüğmeleriniEtkinleştir(false);
            kullanıcıDüğmeleriniEtkinleştir(false);
            kalanAtışHakkı--;
            başlığıAtışSayısıİleGüncelle();
            bilgisayarAtışYap(); 
            return;
        }
        başlığıAtışSayısıİleGüncelle();
    }

    private void bilgisayarAtışYap() { 
        Timer zamanlayıcı = new Timer(1000, null);
        zamanlayıcı.addActionListener(e -> {
            int r = rastgele.nextInt(BOYUT);
            int c = rastgele.nextInt(BOYUT);

            if (kullanıcıTahtası[r][c] == VURULDU || kullanıcıTahtası[r][c] == ISKALANDI) return;

            if (kullanıcıTahtası[r][c] == GEMİ) {
                kullanıcıTahtası[r][c] = VURULDU;
                kullanıcıDüğmeleri[r][c].setBackground(Color.RED);
                kullanıcıDüğmeleri[r][c].setText("X");
                başlığıAtışSayısıİleGüncelle();
                if (galibiyetKontrolü(kullanıcıTahtası)) {
                    JOptionPane.showMessageDialog(this, "😱Bilgisayar kazandı🏆!");
                    tümDüğmeleriDevreDışıBırak();
                    zamanlayıcı.stop();
                    return;
                }
               
            } else {
                kullanıcıTahtası[r][c] = ISKALANDI;
                kullanıcıDüğmeleri[r][c].setBackground(Color.BLUE);
                kullanıcıDüğmeleri[r][c].setText(".");
                kullanıcıSırası = true;
                bilgisayarDüğmeleriniEtkinleştir(true);
                kullanıcıDüğmeleriniEtkinleştir(false);
                kalanAtışHakkı--;
                başlığıAtışSayısıİleGüncelle();
                zamanlayıcı.stop();
                return;
            }
            başlığıAtışSayısıİleGüncelle();
        });
        zamanlayıcı.start();
    }

    private boolean galibiyetKontrolü(int[][] tahta) { 
        for (int i = 0; i < BOYUT; i++) {
            for (int j = 0; j < BOYUT; j++) {
                if (tahta[i][j] == GEMİ) return false;
            }
        }
        return true;
    }

    private void tümDüğmeleriDevreDışıBırak() { 
        bilgisayarDüğmeleriniEtkinleştir(false);
        kullanıcıDüğmeleriniEtkinleştir(false);
    }

    private void bilgisayarDüğmeleriniEtkinleştir(boolean etkinleştir) {
        for (int i = 0; i < BOYUT; i++)
            for (int j = 0; j < BOYUT; j++)
                bilgisayarDüğmeleri[i][j].setEnabled(etkinleştir && bilgisayarTahtası[i][j] != VURULDU && bilgisayarTahtası[i][j] != ISKALANDI);
    }

    private void kullanıcıDüğmeleriniEtkinleştir(boolean etkinleştir) { 
        for (int i = 0; i < BOYUT; i++)
            for (int j = 0; j < BOYUT; j++)
                kullanıcıDüğmeleri[i][j].setEnabled(etkinleştir);
    }

    private void başlığıAtışSayısıİleGüncelle() { 
        String mod = manuelYerleştirmeModu ? "Manuel yerleştirme" : "Otomatik yerleştirme";
        setTitle("Amiral Battı - Kalan Atış: " + kalanAtışHakkı + " (" + mod + ")");
    }

    private void ikonlarıHazırla() { 
        ikon2Yatay = gemiİkonuOluştur(2, false); 
        ikon2Dikey = gemiİkonuOluştur(2, true);
        ikon3Yatay = gemiİkonuOluştur(3, false);
        ikon3Dikey = gemiİkonuOluştur(3, true);
        ikon4Yatay = gemiİkonuOluştur(4, false);
        ikon4Dikey = gemiİkonuOluştur(4, true);
    }
   
    private Icon gemiİkonuOluştur(int uzunluk, boolean dikey) { 
        int genişlik = dikey ? 20 : 20 * uzunluk;
        int yükseklik = dikey ? 20 * uzunluk : 20;

        BufferedImage resim = new BufferedImage(genişlik, yükseklik, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resim.createGraphics();

        g.setColor(Color.GRAY);
        g.fillRoundRect(0, 0, genişlik, yükseklik, 10, 10);

        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(0, 0, genişlik - 1, yükseklik - 1, 10, 10);

        g.dispose();
        return new ImageIcon(resim);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AmiralBattıOyunu::new);
    }
}