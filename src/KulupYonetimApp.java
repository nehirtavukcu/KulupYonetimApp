import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class KulupYonetimApp extends JFrame {

    // Tablo Modelleri
    private DefaultTableModel ogrenciModel;
    private DefaultTableModel kulupModel;
    private DefaultTableModel uyelikModel;
    private DefaultTableModel mekanModel;
    private DefaultTableModel etkinlikModel;
    private DefaultTableModel katilimModel;
    private DefaultTableModel sertifikaModel;

    // Form Bileşenleri - Öğrenci
    private JTextField txtOgrNo, txtAdSoyad, txtBolum, txtEposta;

    // Form Bileşenleri - Etkinlik
    private JComboBox<String> cmbKulup, cmbMekan, cmbSaat;
    private JTextField txtEtkinlikAdi, txtTarih;

    // Form Bileşenleri - Katılım
    private JComboBox<String> cmbOgrenci, cmbEtkinlikKatilim;

    public KulupYonetimApp() {
        setTitle("Öğrenci Kulüpleri ve Etkinlik Yönetim Sistemi (COME 381)");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("1. Öğrenciler (Ekle / Sil)", createOgrencilerPanel());
        tabbedPane.addTab("2. Kulüpler & Üyelikler", createKuluplerVeUyeliklerPanel());
        tabbedPane.addTab("3. Mekanlar", createMekanlarPanel());
        tabbedPane.addTab("4. Etkinlik Planlama", createEtkinlikPanel());
        tabbedPane.addTab("5. Katılım & Sertifika", createKatilimPanel());

        add(tabbedPane);
        tumVerileriYenile();
    }

    // --- SEKME 1: ÖĞRENCİLER (LİSTELEME, EKLEME VE SİLME) ---
    private JPanel createOgrencilerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Yeni Öğrenci Ekle"));

        txtOgrNo = new JTextField();
        txtAdSoyad = new JTextField();
        txtBolum = new JTextField();
        txtEposta = new JTextField();

        JButton btnOgrEkle = new JButton("Öğrenci Ekle");
        btnOgrEkle.setBackground(new Color(34, 139, 34));
        btnOgrEkle.setForeground(Color.WHITE);

        formPanel.add(new JLabel("Öğrenci No:"));
        formPanel.add(new JLabel("Ad Soyad:"));
        formPanel.add(new JLabel("Bölüm:"));
        formPanel.add(new JLabel("E-Posta:"));
        formPanel.add(new JLabel(""));

        formPanel.add(txtOgrNo);
        formPanel.add(txtAdSoyad);
        formPanel.add(txtBolum);
        formPanel.add(txtEposta);
        formPanel.add(btnOgrEkle);

        ogrenciModel = new DefaultTableModel(new String[]{"Öğrenci No (PK)", "Ad Soyad", "Bölüm", "E-Posta"}, 0);
        JTable ogrenciTable = new JTable(ogrenciModel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOgrSil = new JButton("Seçili Öğrenciyi Sil (DELETE)");
        btnOgrSil.setBackground(new Color(180, 20, 40));
        btnOgrSil.setForeground(Color.WHITE);
        bottomPanel.add(btnOgrSil);

        btnOgrEkle.addActionListener(e -> ogrenciEkle());
        btnOgrSil.addActionListener(e -> seciliOgrenciyiSil(ogrenciTable));

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(ogrenciTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    // --- SEKME 2: KULÜPLER VE ÜYELİKLER ---
    private JPanel createKuluplerVeUyeliklerPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel kulupPanel = new JPanel(new BorderLayout());
        kulupPanel.setBorder(BorderFactory.createTitledBorder("Kulupler Tablosu"));
        kulupModel = new DefaultTableModel(new String[]{"Kulüp ID (PK)", "Kulüp Adı", "Kuruluş Yılı", "Danışman Hoca"}, 0);
        kulupPanel.add(new JScrollPane(new JTable(kulupModel)), BorderLayout.CENTER);

        JPanel uyelikPanel = new JPanel(new BorderLayout());
        uyelikPanel.setBorder(BorderFactory.createTitledBorder("Uyelikler Tablosu (N:M İlişki - JOIN)"));
        uyelikModel = new DefaultTableModel(new String[]{"Üyelik ID", "Öğrenci No", "Öğrenci Adı", "Kulüp Adı", "Rol", "Katılım Tarihi"}, 0);
        uyelikPanel.add(new JScrollPane(new JTable(uyelikModel)), BorderLayout.CENTER);

        panel.add(kulupPanel);
        panel.add(uyelikPanel);
        return panel;
    }

    // --- SEKME 3: MEKANLAR ---
    private JPanel createMekanlarPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mekanModel = new DefaultTableModel(new String[]{"Mekan ID (PK)", "Mekan Adı", "Kampüs", "Kapasite"}, 0);
        JTable mekanTable = new JTable(mekanModel);

        panel.add(new JScrollPane(mekanTable), BorderLayout.CENTER);
        return panel;
    }

    // --- SEKME 4: ETKİNLİK PLANLAMA ---
    private JPanel createEtkinlikPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Yeni Etkinlik Planla (Mekan Çakışma Korumalı)"));

        cmbKulup = new JComboBox<>();
        cmbMekan = new JComboBox<>();
        txtEtkinlikAdi = new JTextField();
        txtTarih = new JTextField("2026-11-20");
        cmbSaat = new JComboBox<>(new String[]{"10:00 - 12:00", "13:00 - 15:00", "14:00 - 16:00", "15:00 - 17:00"});
        cmbSaat.setSelectedItem("14:00 - 16:00");

        JButton btnEkle = new JButton("Etkinlik Ekle");
        btnEkle.setBackground(new Color(0, 102, 204));
        btnEkle.setForeground(Color.WHITE);

        formPanel.add(new JLabel("Düzenleyen Kulüp:"));
        formPanel.add(cmbKulup);
        formPanel.add(new JLabel("Mekan:"));
        formPanel.add(cmbMekan);

        formPanel.add(new JLabel("Etkinlik Adı:"));
        formPanel.add(txtEtkinlikAdi);
        formPanel.add(new JLabel("Tarih (YYYY-AA-GG):"));
        formPanel.add(txtTarih);

        formPanel.add(new JLabel("Saat Dilimi:"));
        formPanel.add(cmbSaat);
        formPanel.add(new JLabel(""));
        formPanel.add(btnEkle);

        etkinlikModel = new DefaultTableModel(new String[]{"ID", "Etkinlik Adı", "Kulüp", "Mekan", "Tarih", "Saat"}, 0);
        JTable table = new JTable(etkinlikModel);

        btnEkle.addActionListener(e -> etkinlikEkle());

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // --- SEKME 5: KATILIM VE SERTİFİKA DURUMU ---
    private JPanel createKatilimPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Etkinlik Yoklaması Ekle"));

        cmbOgrenci = new JComboBox<>();
        cmbEtkinlikKatilim = new JComboBox<>();
        JButton btnKatilimEkle = new JButton("Katılımı Kaydet");

        topPanel.add(new JLabel("Öğrenci:"));
        topPanel.add(cmbOgrenci);
        topPanel.add(new JLabel("Etkinlik:"));
        topPanel.add(cmbEtkinlikKatilim);
        topPanel.add(btnKatilimEkle);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        JPanel sertifikaPanel = new JPanel(new BorderLayout());
        sertifikaPanel.setBorder(BorderFactory.createTitledBorder("Sertifika Hak Ediş Raporu (vw_sertifika_durumu VIEW)"));
        sertifikaModel = new DefaultTableModel(new String[]{"Öğrenci No", "Ad Soyad", "Bölüm", "Katılım Sayısı", "Sertifika Durumu"}, 0);
        sertifikaPanel.add(new JScrollPane(new JTable(sertifikaModel)), BorderLayout.CENTER);

        JPanel katilimDetayPanel = new JPanel(new BorderLayout());
        katilimDetayPanel.setBorder(BorderFactory.createTitledBorder("Katilimlar Tablosu (Tüm Yoklama Kayıtları)"));
        katilimModel = new DefaultTableModel(new String[]{"Katılım ID", "Öğrenci No", "Ad Soyad", "Etkinlik Adı", "Yoklama"}, 0);
        katilimDetayPanel.add(new JScrollPane(new JTable(katilimModel)), BorderLayout.CENTER);

        splitPane.setTopComponent(sertifikaPanel);
        splitPane.setBottomComponent(katilimDetayPanel);
        splitPane.setDividerLocation(230);

        btnKatilimEkle.addActionListener(e -> katilimKaydet());

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    // --- VERİTABANI OKUMA (SELECT) METOTLARI ---
    private void tumVerileriYenile() {
        loadOgrenciler();
        loadKulupler();
        loadUyelikler();
        loadMekanlar();
        loadEtkinlikler();
        loadKatilimlar();
        loadSertifikaDurumu();
        loadComboBoxes();
    }

    private void loadOgrenciler() {
        ogrenciModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Ogrenciler")) {
            while (rs.next()) {
                ogrenciModel.addRow(new Object[]{
                        rs.getString("ogrenci_no"),
                        rs.getString("ad_soyad"),
                        rs.getString("bolum"),
                        rs.getString("eposta")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Bağlantı Hatası: " + ex.getMessage());
        }
    }

    private void loadKulupler() {
        kulupModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Kulupler")) {
            while (rs.next()) {
                kulupModel.addRow(new Object[]{
                        rs.getInt("kulup_id"),
                        rs.getString("kulup_adi"),
                        rs.getInt("kurulus_yili"),
                        rs.getString("danisman_hoca")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadUyelikler() {
        uyelikModel.setRowCount(0);
        String sql = "SELECT u.uyelik_id, o.ogrenci_no, o.ad_soyad, k.kulup_adi, u.rol, u.katilim_tarihi " +
                "FROM Uyelikler u " +
                "JOIN Ogrenciler o ON u.ogrenci_no = o.ogrenci_no " +
                "JOIN Kulupler k ON u.kulup_id = k.kulup_id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                uyelikModel.addRow(new Object[]{
                        rs.getInt("uyelik_id"),
                        rs.getString("ogrenci_no"),
                        rs.getString("ad_soyad"),
                        rs.getString("kulup_adi"),
                        rs.getString("rol"),
                        rs.getDate("katilim_tarihi")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadMekanlar() {
        mekanModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Mekanlar")) {
            while (rs.next()) {
                mekanModel.addRow(new Object[]{
                        rs.getInt("mekan_id"),
                        rs.getString("mekan_adi"),
                        rs.getString("kampus"),
                        rs.getInt("kapasite")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadEtkinlikler() {
        etkinlikModel.setRowCount(0);
        String sql = "SELECT e.etkinlik_id, e.etkinlik_adi, k.kulup_adi, m.mekan_adi, e.etkinlik_tarihi, e.saat_dilimi " +
                "FROM Etkinlikler e " +
                "JOIN Kulupler k ON e.kulup_id = k.kulup_id " +
                "JOIN Mekanlar m ON e.mekan_id = m.mekan_id " +
                "ORDER BY e.etkinlik_tarihi ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                etkinlikModel.addRow(new Object[]{
                        rs.getInt("etkinlik_id"),
                        rs.getString("etkinlik_adi"),
                        rs.getString("kulup_adi"),
                        rs.getString("mekan_adi"),
                        rs.getDate("etkinlik_tarihi"),
                        rs.getString("saat_dilimi")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadKatilimlar() {
        katilimModel.setRowCount(0);
        String sql = "SELECT kt.katilim_id, o.ogrenci_no, o.ad_soyad, e.etkinlik_adi, kt.yoklama_durumu " +
                "FROM Katilimlar kt " +
                "JOIN Ogrenciler o ON kt.ogrenci_no = o.ogrenci_no " +
                "JOIN Etkinlikler e ON kt.etkinlik_id = e.etkinlik_id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                katilimModel.addRow(new Object[]{
                        rs.getInt("katilim_id"),
                        rs.getString("ogrenci_no"),
                        rs.getString("ad_soyad"),
                        rs.getString("etkinlik_adi"),
                        rs.getBoolean("yoklama_durumu") ? "Katıldı" : "Katılmadı"
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadSertifikaDurumu() {
        sertifikaModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM vw_sertifika_durumu")) {
            while (rs.next()) {
                sertifikaModel.addRow(new Object[]{
                        rs.getString("ogrenci_no"),
                        rs.getString("ad_soyad"),
                        rs.getString("bolum"),
                        rs.getInt("katilim_sayisi"),
                        rs.getString("sertifika_durumu")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadComboBoxes() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            cmbKulup.removeAllItems();
            ResultSet rs = stmt.executeQuery("SELECT kulup_id, kulup_adi FROM Kulupler");
            while (rs.next()) cmbKulup.addItem(rs.getInt("kulup_id") + " - " + rs.getString("kulup_adi"));

            cmbMekan.removeAllItems();
            rs = stmt.executeQuery("SELECT mekan_id, mekan_adi FROM Mekanlar");
            while (rs.next()) cmbMekan.addItem(rs.getInt("mekan_id") + " - " + rs.getString("mekan_adi"));

            cmbOgrenci.removeAllItems();
            rs = stmt.executeQuery("SELECT ogrenci_no, ad_soyad FROM Ogrenciler");
            while (rs.next()) cmbOgrenci.addItem(rs.getString("ogrenci_no") + " - " + rs.getString("ad_soyad"));

            cmbEtkinlikKatilim.removeAllItems();
            rs = stmt.executeQuery("SELECT etkinlik_id, etkinlik_adi FROM Etkinlikler");
            while (rs.next()) cmbEtkinlikKatilim.addItem(rs.getInt("etkinlik_id") + " - " + rs.getString("etkinlik_adi"));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // --- EKLEME (INSERT) VE SİLME (DELETE) İŞLEMLERİ ---
    private void ogrenciEkle() {
        String no = txtOgrNo.getText().trim();
        String ad = txtAdSoyad.getText().trim();
        String bolum = txtBolum.getText().trim();
        String eposta = txtEposta.getText().trim();

        if (no.isEmpty() || ad.isEmpty() || bolum.isEmpty() || eposta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm öğrenci bilgilerini doldurun.");
            return;
        }

        String sql = "INSERT INTO Ogrenciler (ogrenci_no, ad_soyad, bolum, eposta) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, no);
            pstmt.setString(2, ad);
            pstmt.setString(3, bolum);
            pstmt.setString(4, eposta);
            pstmt.executeUpdate();

            txtOgrNo.setText("");
            txtAdSoyad.setText("");
            txtBolum.setText("");
            txtEposta.setText("");
            tumVerileriYenile();
            JOptionPane.showMessageDialog(this, "Öğrenci başarıyla eklendi!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ekleme Hatası: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seciliOgrenciyiSil(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen tablodan silmek istediğiniz öğrenciyi seçin.");
            return;
        }

        String ogrenciNo = ogrenciModel.getValueAt(selectedRow, 0).toString();
        String adSoyad = ogrenciModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                adSoyad + " (" + ogrenciNo + ") kaydını silmek istediğinize emin misiniz?\n(Bağlı üyelik ve katılım kayıtları da CASCADE ile silinecektir.)",
                "Öğrenci Silme Onayı", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Ogrenciler WHERE ogrenci_no = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, ogrenciNo);
                pstmt.executeUpdate();

                tumVerileriYenile();
                JOptionPane.showMessageDialog(this, "Öğrenci kaydı veritabanından silindi.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Silme Hatası: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void etkinlikEkle() {
        if (txtEtkinlikAdi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen etkinlik adını girin.");
            return;
        }

        int kulupId = Integer.parseInt(cmbKulup.getSelectedItem().toString().split(" - ")[0]);
        int mekanId = Integer.parseInt(cmbMekan.getSelectedItem().toString().split(" - ")[0]);
        String etkinlikAdi = txtEtkinlikAdi.getText().trim();
        String tarih = txtTarih.getText().trim();
        String saat = cmbSaat.getSelectedItem().toString();

        String sql = "INSERT INTO Etkinlikler (kulup_id, mekan_id, etkinlik_adi, etkinlik_tarihi, saat_dilimi) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, kulupId);
            pstmt.setInt(2, mekanId);
            pstmt.setString(3, etkinlikAdi);
            pstmt.setDate(4, Date.valueOf(tarih));
            pstmt.setString(5, saat);
            pstmt.executeUpdate();

            txtEtkinlikAdi.setText("");
            tumVerileriYenile();
            JOptionPane.showMessageDialog(this, "Etkinlik başarıyla planlandı!");
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this,
                    "MEKAN ÇAKIŞMASI! Seçilen mekan, tarih ve saat diliminde başka bir etkinlik mevcut.\n(Veritabanı Kısıtlaması: uk_mekan_cakisma)",
                    "İş Kuralı İhlali", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void katilimKaydet() {
        String ogrenciNo = cmbOgrenci.getSelectedItem().toString().split(" - ")[0];
        int etkinlikId = Integer.parseInt(cmbEtkinlikKatilim.getSelectedItem().toString().split(" - ")[0]);

        String sql = "INSERT INTO Katilimlar (ogrenci_no, etkinlik_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ogrenciNo);
            pstmt.setInt(2, etkinlikId);
            pstmt.executeUpdate();

            tumVerileriYenile();
            JOptionPane.showMessageDialog(this, "Katılım kaydedildi ve sertifika durumu güncellendi!");
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Bu öğrenci zaten bu etkinliğe katılmış görünüyor!", "Mükerrer Kayıt", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KulupYonetimApp().setVisible(true));
    }
}