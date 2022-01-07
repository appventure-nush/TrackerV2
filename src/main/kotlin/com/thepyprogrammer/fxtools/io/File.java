package com.thepyprogrammer.fxtools.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import javafx.stage.*;

public class File extends java.io.File implements Cloneable, AutoCloseable {
    public PrintWriter out = null;
    public Scanner in = null;
    public FileOutputStream outstream = null;
    private char type;

    // JavaFX Integration
    private static FileChooser fc = new FileChooser();
    private static DirectoryChooser dc = new DirectoryChooser();

    // Ensuring only a singular in, out exists per file
    private static HashMap<String, Scanner> scanners = new HashMap<>();
    private static HashMap<String, PrintWriter> appendwriters = new HashMap<>();
    private static HashMap<String, PrintWriter> overwriters = new HashMap<>();
    private static HashMap<String, FileOutputStream> writeoutstreams = new HashMap<>();
    private static HashMap<String, FileOutputStream> appendoutstreams = new HashMap<>();

    // Extension Filters
    public static FileChooser.ExtensionFilter PNG = new FileChooser.ExtensionFilter("Portable Newtwork Graphics (PNG) Files", "*.png"),
            ICO = new FileChooser.ExtensionFilter("ICO Files", "*.ico"),
            JPG = new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg"),
            GIF = new FileChooser.ExtensionFilter("Graphics Interchange Format (GIF) Files", "*.gif"),
            JFIF = new FileChooser.ExtensionFilter("JPEG File Interchange Format (JFIF) Files", "*.jfif"),
            PDF = new FileChooser.ExtensionFilter("Portable Document Format (PDF) Files", "*.pdf"),
            HTML = new FileChooser.ExtensionFilter("HyperText Markup Language (HTML) Files", "*.htm", "*.html"),
            XHTML = new FileChooser.ExtensionFilter("Extensible HyperText Markup Language (XHTML) Files", "*.xhtml"),
            PHTML = new FileChooser.ExtensionFilter("PHP HyperText Markup Language (PHTML) Files", "*.phtml"),
            XML = new FileChooser.ExtensionFilter("Extensible Markup Language (XML) Files", "*.xml"),
            FXML = new FileChooser.ExtensionFilter("JavaFX XML Files", "*.fxml"),
            PHP = new FileChooser.ExtensionFilter("PHP Files", "*.php", "*.php5"),
            CSS = new FileChooser.ExtensionFilter("Cascading Style Sheets (CSS) Files", "*.css"),
            JS = new FileChooser.ExtensionFilter("JavaScript (JS) Files", "*.js", "*.jsm"),
            JSON = new FileChooser.ExtensionFilter("JSON Files", "*.json"),
            JAVA = new FileChooser.ExtensionFilter("Java Files", "*.java"),
            KOTLIN = new FileChooser.ExtensionFilter("Kotlin Files", "*.kt"),
            SCALA = new FileChooser.ExtensionFilter("Scala Files", "*.scala"),
            PYTHON = new FileChooser.ExtensionFilter("Python Files", "*.py", "*.pyc", "*.pyw"),
            RUBY = new FileChooser.ExtensionFilter("Ruby Files", "*.rb", "*.rbw", "*.rake", "*.rbx"),
            C = new FileChooser.ExtensionFilter("C Files", "*.c", "*.h", "*.idc"),
            CPP = new FileChooser.ExtensionFilter("C++ Files", "*.cpp", "*.hpp", "*.c++", "*.h++", "*.cc", "*.hh", "*.cxx", "*.hxx", "*.C", "*.H", "*.cp", "*.cpp"),
            CSHARP = new FileChooser.ExtensionFilter("C# Files", "*.cs"),
            INO = new FileChooser.ExtensionFilter("Arduino Files", "*.ino"),
            VB = new FileChooser.ExtensionFilter("Visual Basic Files", "*.vbs"),
            GO = new FileChooser.ExtensionFilter("Golang Files", "*.go"),
            RUST = new FileChooser.ExtensionFilter("Rust Files", "*.rs"),
            SWIFT = new FileChooser.ExtensionFilter("Swift Files", "*.swift"),
            TEXT = new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.text"),
            MD = new FileChooser.ExtensionFilter("Markdown (MD) Files", "*.md"),
            README = new FileChooser.ExtensionFilter("README Files", "*.README"),
            BIBTEX = new FileChooser.ExtensionFilter("BibTeX Files", "*.bib"),
            LATEX = new FileChooser.ExtensionFilter("LaTeX Files", "*.tex", "*.aux", "*.toc"),
            SQL = new FileChooser.ExtensionFilter("Structured Query Language (SQL) Files", "*.sql");

    // Setting Initial Directory
    static {
        fc.setInitialDirectory(new java.io.File(System.getProperty("user.dir")));
        dc.setInitialDirectory(new java.io.File(System.getProperty("user.dir")));
    }


    // Constructors
    public File(String filename) {
        this(filename, 'r');
    }

    public File(String filename, char type) {
        super(filename);
        try {
            if (!exists()) createNewFile();
        } catch(IOException ex) {}
        this.type = type;
        if(!isDirectory()) {
            if ((type == 'r' || type == 'w' || type == 'a') && Files.isReadable(toPath())) {
                if(scanners.containsKey(getAbsolutePath())) in = scanners.get(getAbsolutePath());
                else {
                    try { in = new Scanner(this); scanners.put(getAbsolutePath(), in); } catch (IOException e) {}
                }
            }
            if (type == 'w' && Files.isWritable(toPath())) {
                if(writeoutstreams.containsKey(getAbsolutePath()) && overwriters.containsKey(getAbsolutePath())) {
                    outstream = writeoutstreams.get(getAbsolutePath());
                    out = overwriters.get(getAbsolutePath());
                } else {
                    try {
                        outstream = new FileOutputStream(filename);
                        out = new PrintWriter(outstream);
                        writeoutstreams.put(getAbsolutePath(), outstream);
                        overwriters.put(getAbsolutePath(), out);
                    } catch (IOException e) {}
                }
            } else if (type == 'a' && Files.isWritable(toPath())) {
                if(appendoutstreams.containsKey(getAbsolutePath()) && appendwriters.containsKey(getAbsolutePath())) {
                    outstream = appendoutstreams.get(getAbsolutePath());
                    out = appendwriters.get(getAbsolutePath());
                } else {
                    try {
                        outstream = new FileOutputStream(filename, true);
                        out = new PrintWriter(outstream);
                        appendoutstreams.put(getAbsolutePath(), outstream);
                        appendwriters.put(getAbsolutePath(), out);
                    } catch (IOException e) {}
                }
            }
        }
    }

    public File() { this('r'); }
    public File(char type) { this(getFile(), type); }

    public File(java.io.File file) { this(file.getAbsolutePath()); }
    public File(java.io.File file, char type) { this(file.getAbsolutePath(), type); }

    public File(URL url) { this(url.getFile()); }
    public File(URL url, char type) { this(url.getFile(), type); }

    public File(URI uri) throws MalformedURLException { this(uri.toURL().getFile()); }
    public File(URI uri, char type) throws MalformedURLException { this(uri.toURL().getFile(), type); }

    public static File read(String filename) { return new File(filename); }
    public static File read(File file) { return new File(file); }
    public static File read(java.io.File file) { return new File(file); }
    public static File read(URI uri) throws MalformedURLException { return new File(uri); }
    public static File read(URL url) { return new File(url); }

    public static File write(String filename) { return new File(filename, 'w'); }
    public static File write(File file) { return new File(file, 'w'); }
    public static File write(java.io.File file) { return new File(file, 'w'); }
    public static File write(URI uri) throws MalformedURLException { return new File(uri, 'w'); }
    public static File write(URL url) { return new File(url, 'w'); }

    public static File append(String filename) { return new File(filename, 'a'); }
    public static File append(File file) { return new File(file, 'a'); }
    public static File append(java.io.File file) { return new File(file, 'a'); }
    public static File append(URI uri) throws MalformedURLException { return new File(uri, 'a'); }
    public static File append(URL url) { return new File(url, 'a'); }


    // Methods
    public String getName() { return getAbsolutePath(); }
    public File getAbsoluteFile() { return new File(super.getAbsoluteFile()); }
    public File getParentFile() { return new File(super.getParentFile()); }
    public File getCanonicalFile() {
        try { return new File(super.getCanonicalFile()); } catch (IOException e) { return null; }
    }

    public boolean rename(File dest) { return renameTo(dest); }

    public File[] listFiles() { return convert(listFiles()); }

    public String getRelativePath() { return new File(System.getProperty("user.dir")).relativize(this); }
    public String getRelativePath(String directory) { return new File(directory).relativize(this); }
    public String getRelativePath(File directory) { return directory.relativize(this); }
    public String getRelativePath(java.io.File directory) { return directory.toURI().relativize(this.toURI()).getPath(); }
    public String getRelativePath(URI directory) { return directory.relativize(this.toURI()).getPath(); }
    public String getRelativePath(URL directory) throws URISyntaxException { return directory.toURI().relativize(this.toURI()).getPath(); }

    public static File[] convert(java.io.File ...files) { return convert(Arrays.asList(files)); }

    public static File[] convert(Collection<java.io.File> files) {
        File[] rearr = new File[files.size()];
        int i = 0;
        for(java.io.File file: files) {
            rearr[i] = new File(file);
            i++;
        }
        return rearr;
    }

    public static File getFile(Stage stage) { return convert(fc.showOpenDialog(stage))[0]; }
    public static File[] getFiles(Stage stage) { return convert(fc.showOpenMultipleDialog(stage)); }

    public static File getFile() { return getFile(new Stage()); }
    public static File[] getFiles() { return getFiles(new Stage()); }

    public static File getFile(Stage stage, FileChooser.ExtensionFilter...extensions) { return getFile(stage, Arrays.asList(extensions)); }

    public static File getFile(Stage stage, Collection<FileChooser.ExtensionFilter> extensions) {
        fc.getExtensionFilters().addAll(extensions);
        File file = getFile(stage);
        fc.getExtensionFilters().clear();
        return file;
    }

    public static File getFile(FileChooser.ExtensionFilter...extensions) { return getFile(new Stage(), extensions); }
    public static File getFile(Collection<FileChooser.ExtensionFilter> extensions) { return getFile(new Stage(), extensions); }

    public static File[] getFiles(Stage stage, FileChooser.ExtensionFilter...extensions) { return getFiles(stage, Arrays.asList(extensions)); }

    public static File[] getFiles(Stage stage, Collection<FileChooser.ExtensionFilter> extensions) {
        fc.getExtensionFilters().addAll(extensions);
        File[] files = getFiles(stage);
        fc.getExtensionFilters().clear();
        return files;
    }

    public static File[] getFiles(FileChooser.ExtensionFilter...extensions) { return getFiles(new Stage(), extensions); }
    public static File[] getFiles(Collection<FileChooser.ExtensionFilter> extensions) { return getFiles(new Stage(), extensions); }


    public static File getPNG(Stage stage) { return getFile(stage, PNG); }
    public static File[] getPNGs(Stage stage) { return getFiles(stage, PNG); }
    public static File getPNG() { return getFile(PNG); }
    public static File[] getPNGs() { return getFiles(PNG); }


    public static File getJPG(Stage stage) { return getFile(stage, JPG); }
    public static File[] getJPGs(Stage stage) { return getFiles(stage, JPG); }
    public static File getJPG() { return getFile(JPG); }
    public static File[] getJPGs() { return getFiles(JPG); }


    public static File getICO(Stage stage) { return getFile(stage, ICO); }
    public static File[] getICOs(Stage stage) { return getFiles(stage, ICO); }
    public static File getICO() { return getFile(ICO); }
    public static File[] getICOs() { return getFiles(ICO); }


    public static File getGIF(Stage stage) { return getFile(stage, GIF); }
    public static File[] getGIFs(Stage stage) { return getFiles(stage, GIF); }
    public static File getGIF() { return getFile(GIF); }
    public static File[] getGIFs() { return getFiles(GIF); }


    public static File getJFIF(Stage stage) { return getFile(stage, JFIF); }
    public static File[] getJFIFs(Stage stage) { return getFiles(stage, JFIF); }
    public static File getJFIF() { return getFile(JFIF); }
    public static File[] getJFIFs() { return getFiles(JFIF); }


    public static File getPDF(Stage stage) { return getFile(stage, PDF); }
    public static File[] getPDFs(Stage stage) { return getFiles(stage, PDF);}
    public static File getPDF() { return getFile(PDF); }
    public static File[] getPDFs() { return getFiles(PDF);}


    public static File getHTML(Stage stage) { return getFile(stage, HTML); }
    public static File[] getHTMLs(Stage stage) { return getFiles(stage, HTML);}
    public static File getHTML() { return getFile(HTML); }
    public static File[] getHTMLs() { return getFiles(HTML);}


    public static File getXHTML(Stage stage) { return getFile(stage, XHTML); }
    public static File[] getXHTMLs(Stage stage) { return getFiles(stage, XHTML);}
    public static File getXHTML() { return getFile(XHTML); }
    public static File[] getXHTMLs() { return getFiles(XHTML);}


    public static File getPHTML(Stage stage) { return getFile(stage, PHTML); }
    public static File[] getPHTMLs(Stage stage) { return getFiles(stage, PHTML);}
    public static File getPHTML() { return getFile(PHTML); }
    public static File[] getPHTMLs() { return getFiles(PHTML);}


    public static File getXML(Stage stage) { return getFile(stage, XML); }
    public static File[] getXMLs(Stage stage) { return getFiles(stage, XML);}
    public static File getXML() { return getFile(XML); }
    public static File[] getXMLs() { return getFiles(XML);}


    public static File getFXML(Stage stage) { return getFile(stage, FXML); }
    public static File[] getFXMLs(Stage stage) { return getFiles(stage, FXML);}
    public static File getFXML() { return getFile(FXML); }
    public static File[] getFXMLs() { return getFiles(FXML);}


    public static File getPHP(Stage stage) { return getFile(stage, PHP); }
    public static File[] getPHPs(Stage stage) { return getFiles(stage, PHP);}


    public static File getCSS(Stage stage) { return getFile(stage, CSS); }
    public static File[] getCSSs(Stage stage) { return getFiles(stage, CSS);}


    public static File getJS(Stage stage) { return getFile(stage, JS); }
    public static File[] getJSs(Stage stage) { return getFiles(stage, JS);}


    public static File getJSON(Stage stage) { return getFile(stage, JSON); }
    public static File[] getJSONs(Stage stage) { return getFiles(stage, JSON);}


    public static File getJAVA(Stage stage) { return getFile(stage, JAVA); }
    public static File[] getJAVAs(Stage stage) { return getFiles(stage, JAVA);}


    public static File getKOTLIN(Stage stage) { return getFile(stage, KOTLIN); }
    public static File[] getKOTLINs(Stage stage) { return getFiles(stage, KOTLIN);}


    public static File getSCALA(Stage stage) { return getFile(stage, SCALA); }
    public static File[] getSCALAs(Stage stage) { return getFiles(stage, SCALA);}


    public static File getPYTHON(Stage stage) { return getFile(stage, PYTHON); }
    public static File[] getPYTHONs(Stage stage) { return getFiles(stage, PYTHON);}


    public static File getRUBY(Stage stage) { return getFile(stage, RUBY); }
    public static File[] getRUBYs(Stage stage) { return getFiles(stage, RUBY);}


    public static File getC(Stage stage) { return getFile(stage, C); }
    public static File[] getCs(Stage stage) { return getFiles(stage, C);}


    public static File getCPP(Stage stage) { return getFile(stage, CPP); }
    public static File[] getCPPs(Stage stage) { return getFiles(stage, CPP);}


    public static File getCSHARP(Stage stage) { return getFile(stage, CSHARP); }
    public static File[] getCSHARPs(Stage stage) { return getFiles(stage, CSHARP);}


    public static File getINO(Stage stage) { return getFile(stage, INO); }
    public static File[] getINOs(Stage stage) { return getFiles(stage, INO);}


    public static File getVB(Stage stage) { return getFile(stage, VB); }
    public static File[] getVBs(Stage stage) { return getFiles(stage, VB);}


    public static File getGO(Stage stage) { return getFile(stage, GO); }
    public static File[] getGOs(Stage stage) { return getFiles(stage, GO);}


    public static File getRUST(Stage stage) { return getFile(stage, RUST); }
    public static File[] getRUSTs(Stage stage) { return getFiles(stage, RUST);}


    public static File getSWIFT(Stage stage) { return getFile(stage, SWIFT); }
    public static File[] getSWIFTs(Stage stage) { return getFiles(stage, SWIFT);}


    public static File getTEXT(Stage stage) { return getFile(stage, TEXT); }
    public static File[] getTEXTs(Stage stage) { return getFiles(stage, TEXT);}


    public static File getMD(Stage stage) { return getFile(stage, MD); }
    public static File[] getMDs(Stage stage) { return getFiles(stage, MD);}


    public static File getREADME(Stage stage) { return getFile(stage, README); }
    public static File[] getREADMEs(Stage stage) { return getFiles(stage, README);}


    public static File getBIBTEX(Stage stage) { return getFile(stage, BIBTEX); }
    public static File[] getBIBTEXs(Stage stage) { return getFiles(stage, BIBTEX);}


    public static File getLATEX(Stage stage) { return getFile(stage, LATEX); }
    public static File[] getLATEXs(Stage stage) { return getFiles(stage, LATEX);}


    public static File getSQL(Stage stage) { return getFile(stage, SQL); }
    public static File[] getSQLs(Stage stage) { return getFiles(stage, SQL);}

    public static File getPHP() { return getFile(PHP); }
    public static File[] getPHPs() { return getFiles(PHP);}


    public static File getCSS() { return getFile(CSS); }
    public static File[] getCSSs() { return getFiles(CSS);}


    public static File getJS() { return getFile(JS); }
    public static File[] getJSs() { return getFiles(JS);}


    public static File getJSON() { return getFile(JSON); }
    public static File[] getJSONs() { return getFiles(JSON);}


    public static File getJAVA() { return getFile(JAVA); }
    public static File[] getJAVAs() { return getFiles(JAVA);}


    public static File getKOTLIN() { return getFile(KOTLIN); }
    public static File[] getKOTLINs() { return getFiles(KOTLIN);}


    public static File getSCALA() { return getFile(SCALA); }
    public static File[] getSCALAs() { return getFiles(SCALA);}


    public static File getPYTHON() { return getFile(PYTHON); }
    public static File[] getPYTHONs() { return getFiles(PYTHON);}


    public static File getRUBY() { return getFile(RUBY); }
    public static File[] getRUBYs() { return getFiles(RUBY);}


    public static File getC() { return getFile(C); }
    public static File[] getCs() { return getFiles(C);}


    public static File getCPP() { return getFile(CPP); }
    public static File[] getCPPs() { return getFiles(CPP);}


    public static File getCSHARP() { return getFile(CSHARP); }
    public static File[] getCSHARPs() { return getFiles(CSHARP);}


    public static File getINO() { return getFile(INO); }
    public static File[] getINOs() { return getFiles(INO);}


    public static File getVB() { return getFile(VB); }
    public static File[] getVBs() { return getFiles(VB);}


    public static File getGO() { return getFile(GO); }
    public static File[] getGOs() { return getFiles(GO);}


    public static File getRUST() { return getFile(RUST); }
    public static File[] getRUSTs() { return getFiles(RUST);}


    public static File getSWIFT() { return getFile(SWIFT); }
    public static File[] getSWIFTs() { return getFiles(SWIFT);}


    public static File getTEXT() { return getFile(TEXT); }
    public static File[] getTEXTs() { return getFiles(TEXT);}


    public static File getMD() { return getFile(MD); }
    public static File[] getMDs() { return getFiles(MD);}


    public static File getREADME() { return getFile(README); }
    public static File[] getREADMEs() { return getFiles(README);}


    public static File getBIBTEX() { return getFile(BIBTEX); }
    public static File[] getBIBTEXs() { return getFiles(BIBTEX);}


    public static File getLATEX() { return getFile(LATEX); }
    public static File[] getLATEXs() { return getFiles(LATEX);}


    public static File getSQL() { return getFile(SQL); }
    public static File[] getSQLs() { return getFiles(SQL);}

    public static File openFile(Stage stage) { return convert(fc.showOpenDialog(stage))[0]; }
    public static File[] openFiles(Stage stage) { return convert(fc.showOpenMultipleDialog(stage)); }

    public static File openFile() { return openFile(new Stage()); }
    public static File[] openFiles() { return openFiles(new Stage()); }

    public static File openFile(Stage stage, FileChooser.ExtensionFilter...extensions) { return openFile(stage, Arrays.asList(extensions)); }

    public static File openFile(Stage stage, Collection<FileChooser.ExtensionFilter> extensions) {
        fc.getExtensionFilters().addAll(extensions);
        File file = openFile(stage);
        fc.getExtensionFilters().clear();
        return file;
    }

    public static File openFile(FileChooser.ExtensionFilter...extensions) { return openFile(new Stage(), extensions); }
    public static File openFile(Collection<FileChooser.ExtensionFilter> extensions) { return openFile(new Stage(), extensions); }

    public static File[] openFiles(Stage stage, FileChooser.ExtensionFilter...extensions) { return openFiles(stage, Arrays.asList(extensions)); }

    public static File[] openFiles(Stage stage, Collection<FileChooser.ExtensionFilter> extensions) {
        fc.getExtensionFilters().addAll(extensions);
        File[] files = openFiles(stage);
        fc.getExtensionFilters().clear();
        return files;
    }

    public static File[] openFiles(FileChooser.ExtensionFilter...extensions) { return openFiles(new Stage(), extensions); }
    public static File[] openFiles(Collection<FileChooser.ExtensionFilter> extensions) { return openFiles(new Stage(), extensions); }


    public static File openPNG(Stage stage) { return openFile(stage, PNG); }
    public static File[] openPNGs(Stage stage) { return openFiles(stage, PNG); }
    public static File openPNG() { return openFile(PNG); }
    public static File[] openPNGs() { return openFiles(PNG); }


    public static File openJPG(Stage stage) { return openFile(stage, JPG); }
    public static File[] openJPGs(Stage stage) { return openFiles(stage, JPG); }
    public static File openJPG() { return openFile(JPG); }
    public static File[] openJPGs() { return openFiles(JPG); }


    public static File openICO(Stage stage) { return openFile(stage, ICO); }
    public static File[] openICOs(Stage stage) { return openFiles(stage, ICO); }
    public static File openICO() { return openFile(ICO); }
    public static File[] openICOs() { return openFiles(ICO); }


    public static File openGIF(Stage stage) { return openFile(stage, GIF); }
    public static File[] openGIFs(Stage stage) { return openFiles(stage, GIF); }
    public static File openGIF() { return openFile(GIF); }
    public static File[] openGIFs() { return openFiles(GIF); }


    public static File openJFIF(Stage stage) { return openFile(stage, JFIF); }
    public static File[] openJFIFs(Stage stage) { return openFiles(stage, JFIF); }
    public static File openJFIF() { return openFile(JFIF); }
    public static File[] openJFIFs() { return openFiles(JFIF); }


    public static File openPDF(Stage stage) { return openFile(stage, PDF); }
    public static File[] openPDFs(Stage stage) { return openFiles(stage, PDF);}
    public static File openPDF() { return openFile(PDF); }
    public static File[] openPDFs() { return openFiles(PDF);}


    public static File openHTML(Stage stage) { return openFile(stage, HTML); }
    public static File[] openHTMLs(Stage stage) { return openFiles(stage, HTML);}
    public static File openHTML() { return openFile(HTML); }
    public static File[] openHTMLs() { return openFiles(HTML);}


    public static File openXHTML(Stage stage) { return openFile(stage, XHTML); }
    public static File[] openXHTMLs(Stage stage) { return openFiles(stage, XHTML);}
    public static File openXHTML() { return openFile(XHTML); }
    public static File[] openXHTMLs() { return openFiles(XHTML);}


    public static File openPHTML(Stage stage) { return openFile(stage, PHTML); }
    public static File[] openPHTMLs(Stage stage) { return openFiles(stage, PHTML);}
    public static File openPHTML() { return openFile(PHTML); }
    public static File[] openPHTMLs() { return openFiles(PHTML);}


    public static File openXML(Stage stage) { return openFile(stage, XML); }
    public static File[] openXMLs(Stage stage) { return openFiles(stage, XML);}
    public static File openXML() { return openFile(XML); }
    public static File[] openXMLs() { return openFiles(XML);}


    public static File openFXML(Stage stage) { return openFile(stage, FXML); }
    public static File[] openFXMLs(Stage stage) { return openFiles(stage, FXML);}
    public static File openFXML() { return openFile(FXML); }
    public static File[] openFXMLs() { return openFiles(FXML);}


    public static File openPHP(Stage stage) { return openFile(stage, PHP); }
    public static File[] openPHPs(Stage stage) { return openFiles(stage, PHP);}


    public static File openCSS(Stage stage) { return openFile(stage, CSS); }
    public static File[] openCSSs(Stage stage) { return openFiles(stage, CSS);}


    public static File openJS(Stage stage) { return openFile(stage, JS); }
    public static File[] openJSs(Stage stage) { return openFiles(stage, JS);}


    public static File openJSON(Stage stage) { return openFile(stage, JSON); }
    public static File[] openJSONs(Stage stage) { return openFiles(stage, JSON);}


    public static File openJAVA(Stage stage) { return openFile(stage, JAVA); }
    public static File[] openJAVAs(Stage stage) { return openFiles(stage, JAVA);}


    public static File openKOTLIN(Stage stage) { return openFile(stage, KOTLIN); }
    public static File[] openKOTLINs(Stage stage) { return openFiles(stage, KOTLIN);}


    public static File openSCALA(Stage stage) { return openFile(stage, SCALA); }
    public static File[] openSCALAs(Stage stage) { return openFiles(stage, SCALA);}


    public static File openPYTHON(Stage stage) { return openFile(stage, PYTHON); }
    public static File[] openPYTHONs(Stage stage) { return openFiles(stage, PYTHON);}


    public static File openRUBY(Stage stage) { return openFile(stage, RUBY); }
    public static File[] openRUBYs(Stage stage) { return openFiles(stage, RUBY);}


    public static File openC(Stage stage) { return openFile(stage, C); }
    public static File[] openCs(Stage stage) { return openFiles(stage, C);}


    public static File openCPP(Stage stage) { return openFile(stage, CPP); }
    public static File[] openCPPs(Stage stage) { return openFiles(stage, CPP);}


    public static File openCSHARP(Stage stage) { return openFile(stage, CSHARP); }
    public static File[] openCSHARPs(Stage stage) { return openFiles(stage, CSHARP);}


    public static File openINO(Stage stage) { return openFile(stage, INO); }
    public static File[] openINOs(Stage stage) { return openFiles(stage, INO);}


    public static File openVB(Stage stage) { return openFile(stage, VB); }
    public static File[] openVBs(Stage stage) { return openFiles(stage, VB);}


    public static File openGO(Stage stage) { return openFile(stage, GO); }
    public static File[] openGOs(Stage stage) { return openFiles(stage, GO);}


    public static File openRUST(Stage stage) { return openFile(stage, RUST); }
    public static File[] openRUSTs(Stage stage) { return openFiles(stage, RUST);}


    public static File openSWIFT(Stage stage) { return openFile(stage, SWIFT); }
    public static File[] openSWIFTs(Stage stage) { return openFiles(stage, SWIFT);}


    public static File openTEXT(Stage stage) { return openFile(stage, TEXT); }
    public static File[] openTEXTs(Stage stage) { return openFiles(stage, TEXT);}


    public static File openMD(Stage stage) { return openFile(stage, MD); }
    public static File[] openMDs(Stage stage) { return openFiles(stage, MD);}


    public static File openREADME(Stage stage) { return openFile(stage, README); }
    public static File[] openREADMEs(Stage stage) { return openFiles(stage, README);}


    public static File openBIBTEX(Stage stage) { return openFile(stage, BIBTEX); }
    public static File[] openBIBTEXs(Stage stage) { return openFiles(stage, BIBTEX);}


    public static File openLATEX(Stage stage) { return openFile(stage, LATEX); }
    public static File[] openLATEXs(Stage stage) { return openFiles(stage, LATEX);}


    public static File openSQL(Stage stage) { return openFile(stage, SQL); }
    public static File[] openSQLs(Stage stage) { return openFiles(stage, SQL);}

    public static File openPHP() { return openFile(PHP); }
    public static File[] openPHPs() { return openFiles(PHP);}


    public static File openCSS() { return openFile(CSS); }
    public static File[] openCSSs() { return openFiles(CSS);}


    public static File openJS() { return openFile(JS); }
    public static File[] openJSs() { return openFiles(JS);}


    public static File openJSON() { return openFile(JSON); }
    public static File[] openJSONs() { return openFiles(JSON);}


    public static File openJAVA() { return openFile(JAVA); }
    public static File[] openJAVAs() { return openFiles(JAVA);}


    public static File openKOTLIN() { return openFile(KOTLIN); }
    public static File[] openKOTLINs() { return openFiles(KOTLIN);}


    public static File openSCALA() { return openFile(SCALA); }
    public static File[] openSCALAs() { return openFiles(SCALA);}


    public static File openPYTHON() { return openFile(PYTHON); }
    public static File[] openPYTHONs() { return openFiles(PYTHON);}


    public static File openRUBY() { return openFile(RUBY); }
    public static File[] openRUBYs() { return openFiles(RUBY);}


    public static File openC() { return openFile(C); }
    public static File[] openCs() { return openFiles(C);}


    public static File openCPP() { return openFile(CPP); }
    public static File[] openCPPs() { return openFiles(CPP);}


    public static File openCSHARP() { return openFile(CSHARP); }
    public static File[] openCSHARPs() { return openFiles(CSHARP);}


    public static File openINO() { return openFile(INO); }
    public static File[] openINOs() { return openFiles(INO);}


    public static File openVB() { return openFile(VB); }
    public static File[] openVBs() { return openFiles(VB);}


    public static File openGO() { return openFile(GO); }
    public static File[] openGOs() { return openFiles(GO);}


    public static File openRUST() { return openFile(RUST); }
    public static File[] openRUSTs() { return openFiles(RUST);}


    public static File openSWIFT() { return openFile(SWIFT); }
    public static File[] openSWIFTs() { return openFiles(SWIFT);}


    public static File openTEXT() { return openFile(TEXT); }
    public static File[] openTEXTs() { return openFiles(TEXT);}


    public static File openMD() { return openFile(MD); }
    public static File[] openMDs() { return openFiles(MD);}


    public static File openREADME() { return openFile(README); }
    public static File[] openREADMEs() { return openFiles(README);}


    public static File openBIBTEX() { return openFile(BIBTEX); }
    public static File[] openBIBTEXs() { return openFiles(BIBTEX);}


    public static File openLATEX() { return openFile(LATEX); }
    public static File[] openLATEXs() { return openFiles(LATEX);}


    public static File openSQL() { return openFile(SQL); }
    public static File[] openSQLs() { return openFiles(SQL);}



    public static File saveFile(Stage stage) { return convert(fc.showOpenDialog(stage))[0]; }
    public static File[] saveFiles(Stage stage) { return convert(fc.showOpenMultipleDialog(stage)); }

    public static File saveFile() { return saveFile(new Stage()); }
    public static File[] saveFiles() { return saveFiles(new Stage()); }

    public static File saveFile(Stage stage, FileChooser.ExtensionFilter...extensions) { return saveFile(stage, Arrays.asList(extensions)); }

    public static File saveFile(Stage stage, Collection<FileChooser.ExtensionFilter> extensions) {
        fc.getExtensionFilters().addAll(extensions);
        File file = saveFile(stage);
        fc.getExtensionFilters().clear();
        return file;
    }

    public static File saveFile(FileChooser.ExtensionFilter...extensions) { return saveFile(new Stage(), extensions); }
    public static File saveFile(Collection<FileChooser.ExtensionFilter> extensions) { return saveFile(new Stage(), extensions); }

    public static File[] saveFiles(Stage stage, FileChooser.ExtensionFilter...extensions) { return saveFiles(stage, Arrays.asList(extensions)); }

    public static File[] saveFiles(Stage stage, Collection<FileChooser.ExtensionFilter> extensions) {
        fc.getExtensionFilters().addAll(extensions);
        File[] files = saveFiles(stage);
        fc.getExtensionFilters().clear();
        return files;
    }

    public static File[] saveFiles(FileChooser.ExtensionFilter...extensions) { return saveFiles(new Stage(), extensions); }
    public static File[] saveFiles(Collection<FileChooser.ExtensionFilter> extensions) { return saveFiles(new Stage(), extensions); }


    public static File savePNG(Stage stage) { return saveFile(stage, PNG); }
    public static File[] savePNGs(Stage stage) { return saveFiles(stage, PNG); }
    public static File savePNG() { return saveFile(PNG); }
    public static File[] savePNGs() { return saveFiles(PNG); }


    public static File saveJPG(Stage stage) { return saveFile(stage, JPG); }
    public static File[] saveJPGs(Stage stage) { return saveFiles(stage, JPG); }
    public static File saveJPG() { return saveFile(JPG); }
    public static File[] saveJPGs() { return saveFiles(JPG); }


    public static File saveICO(Stage stage) { return saveFile(stage, ICO); }
    public static File[] saveICOs(Stage stage) { return saveFiles(stage, ICO); }
    public static File saveICO() { return saveFile(ICO); }
    public static File[] saveICOs() { return saveFiles(ICO); }


    public static File saveGIF(Stage stage) { return saveFile(stage, GIF); }
    public static File[] saveGIFs(Stage stage) { return saveFiles(stage, GIF); }
    public static File saveGIF() { return saveFile(GIF); }
    public static File[] saveGIFs() { return saveFiles(GIF); }


    public static File saveJFIF(Stage stage) { return saveFile(stage, JFIF); }
    public static File[] saveJFIFs(Stage stage) { return saveFiles(stage, JFIF); }
    public static File saveJFIF() { return saveFile(JFIF); }
    public static File[] saveJFIFs() { return saveFiles(JFIF); }


    public static File savePDF(Stage stage) { return saveFile(stage, PDF); }
    public static File[] savePDFs(Stage stage) { return saveFiles(stage, PDF);}
    public static File savePDF() { return saveFile(PDF); }
    public static File[] savePDFs() { return saveFiles(PDF);}


    public static File saveHTML(Stage stage) { return saveFile(stage, HTML); }
    public static File[] saveHTMLs(Stage stage) { return saveFiles(stage, HTML);}
    public static File saveHTML() { return saveFile(HTML); }
    public static File[] saveHTMLs() { return saveFiles(HTML);}


    public static File saveXHTML(Stage stage) { return saveFile(stage, XHTML); }
    public static File[] saveXHTMLs(Stage stage) { return saveFiles(stage, XHTML);}
    public static File saveXHTML() { return saveFile(XHTML); }
    public static File[] saveXHTMLs() { return saveFiles(XHTML);}


    public static File savePHTML(Stage stage) { return saveFile(stage, PHTML); }
    public static File[] savePHTMLs(Stage stage) { return saveFiles(stage, PHTML);}
    public static File savePHTML() { return saveFile(PHTML); }
    public static File[] savePHTMLs() { return saveFiles(PHTML);}


    public static File saveXML(Stage stage) { return saveFile(stage, XML); }
    public static File[] saveXMLs(Stage stage) { return saveFiles(stage, XML);}
    public static File saveXML() { return saveFile(XML); }
    public static File[] saveXMLs() { return saveFiles(XML);}


    public static File saveFXML(Stage stage) { return saveFile(stage, FXML); }
    public static File[] saveFXMLs(Stage stage) { return saveFiles(stage, FXML);}
    public static File saveFXML() { return saveFile(FXML); }
    public static File[] saveFXMLs() { return saveFiles(FXML);}


    public static File savePHP(Stage stage) { return saveFile(stage, PHP); }
    public static File[] savePHPs(Stage stage) { return saveFiles(stage, PHP);}


    public static File saveCSS(Stage stage) { return saveFile(stage, CSS); }
    public static File[] saveCSSs(Stage stage) { return saveFiles(stage, CSS);}


    public static File saveJS(Stage stage) { return saveFile(stage, JS); }
    public static File[] saveJSs(Stage stage) { return saveFiles(stage, JS);}


    public static File saveJSON(Stage stage) { return saveFile(stage, JSON); }
    public static File[] saveJSONs(Stage stage) { return saveFiles(stage, JSON);}


    public static File saveJAVA(Stage stage) { return saveFile(stage, JAVA); }
    public static File[] saveJAVAs(Stage stage) { return saveFiles(stage, JAVA);}


    public static File saveKOTLIN(Stage stage) { return saveFile(stage, KOTLIN); }
    public static File[] saveKOTLINs(Stage stage) { return saveFiles(stage, KOTLIN);}


    public static File saveSCALA(Stage stage) { return saveFile(stage, SCALA); }
    public static File[] saveSCALAs(Stage stage) { return saveFiles(stage, SCALA);}


    public static File savePYTHON(Stage stage) { return saveFile(stage, PYTHON); }
    public static File[] savePYTHONs(Stage stage) { return saveFiles(stage, PYTHON);}


    public static File saveRUBY(Stage stage) { return saveFile(stage, RUBY); }
    public static File[] saveRUBYs(Stage stage) { return saveFiles(stage, RUBY);}


    public static File saveC(Stage stage) { return saveFile(stage, C); }
    public static File[] saveCs(Stage stage) { return saveFiles(stage, C);}


    public static File saveCPP(Stage stage) { return saveFile(stage, CPP); }
    public static File[] saveCPPs(Stage stage) { return saveFiles(stage, CPP);}


    public static File saveCSHARP(Stage stage) { return saveFile(stage, CSHARP); }
    public static File[] saveCSHARPs(Stage stage) { return saveFiles(stage, CSHARP);}


    public static File saveINO(Stage stage) { return saveFile(stage, INO); }
    public static File[] saveINOs(Stage stage) { return saveFiles(stage, INO);}


    public static File saveVB(Stage stage) { return saveFile(stage, VB); }
    public static File[] saveVBs(Stage stage) { return saveFiles(stage, VB);}


    public static File saveGO(Stage stage) { return saveFile(stage, GO); }
    public static File[] saveGOs(Stage stage) { return saveFiles(stage, GO);}


    public static File saveRUST(Stage stage) { return saveFile(stage, RUST); }
    public static File[] saveRUSTs(Stage stage) { return saveFiles(stage, RUST);}


    public static File saveSWIFT(Stage stage) { return saveFile(stage, SWIFT); }
    public static File[] saveSWIFTs(Stage stage) { return saveFiles(stage, SWIFT);}


    public static File saveTEXT(Stage stage) { return saveFile(stage, TEXT); }
    public static File[] saveTEXTs(Stage stage) { return saveFiles(stage, TEXT);}


    public static File saveMD(Stage stage) { return saveFile(stage, MD); }
    public static File[] saveMDs(Stage stage) { return saveFiles(stage, MD);}


    public static File saveREADME(Stage stage) { return saveFile(stage, README); }
    public static File[] saveREADMEs(Stage stage) { return saveFiles(stage, README);}


    public static File saveBIBTEX(Stage stage) { return saveFile(stage, BIBTEX); }
    public static File[] saveBIBTEXs(Stage stage) { return saveFiles(stage, BIBTEX);}


    public static File saveLATEX(Stage stage) { return saveFile(stage, LATEX); }
    public static File[] saveLATEXs(Stage stage) { return saveFiles(stage, LATEX);}


    public static File saveSQL(Stage stage) { return saveFile(stage, SQL); }
    public static File[] saveSQLs(Stage stage) { return saveFiles(stage, SQL);}

    public static File savePHP() { return saveFile(PHP); }
    public static File[] savePHPs() { return saveFiles(PHP);}


    public static File saveCSS() { return saveFile(CSS); }
    public static File[] saveCSSs() { return saveFiles(CSS);}


    public static File saveJS() { return saveFile(JS); }
    public static File[] saveJSs() { return saveFiles(JS);}


    public static File saveJSON() { return saveFile(JSON); }
    public static File[] saveJSONs() { return saveFiles(JSON);}


    public static File saveJAVA() { return saveFile(JAVA); }
    public static File[] saveJAVAs() { return saveFiles(JAVA);}


    public static File saveKOTLIN() { return saveFile(KOTLIN); }
    public static File[] saveKOTLINs() { return saveFiles(KOTLIN);}


    public static File saveSCALA() { return saveFile(SCALA); }
    public static File[] saveSCALAs() { return saveFiles(SCALA);}


    public static File savePYTHON() { return saveFile(PYTHON); }
    public static File[] savePYTHONs() { return saveFiles(PYTHON);}


    public static File saveRUBY() { return saveFile(RUBY); }
    public static File[] saveRUBYs() { return saveFiles(RUBY);}


    public static File saveC() { return saveFile(C); }
    public static File[] saveCs() { return saveFiles(C);}


    public static File saveCPP() { return saveFile(CPP); }
    public static File[] saveCPPs() { return saveFiles(CPP);}


    public static File saveCSHARP() { return saveFile(CSHARP); }
    public static File[] saveCSHARPs() { return saveFiles(CSHARP);}


    public static File saveINO() { return saveFile(INO); }
    public static File[] saveINOs() { return saveFiles(INO);}


    public static File saveVB() { return saveFile(VB); }
    public static File[] saveVBs() { return saveFiles(VB);}


    public static File saveGO() { return saveFile(GO); }
    public static File[] saveGOs() { return saveFiles(GO);}


    public static File saveRUST() { return saveFile(RUST); }
    public static File[] saveRUSTs() { return saveFiles(RUST);}


    public static File saveSWIFT() { return saveFile(SWIFT); }
    public static File[] saveSWIFTs() { return saveFiles(SWIFT);}


    public static File saveTEXT() { return saveFile(TEXT); }
    public static File[] saveTEXTs() { return saveFiles(TEXT);}


    public static File saveMD() { return saveFile(MD); }
    public static File[] saveMDs() { return saveFiles(MD);}


    public static File saveREADME() { return saveFile(README); }
    public static File[] saveREADMEs() { return saveFiles(README);}


    public static File saveBIBTEX() { return saveFile(BIBTEX); }
    public static File[] saveBIBTEXs() { return saveFiles(BIBTEX);}


    public static File saveLATEX() { return saveFile(LATEX); }
    public static File[] saveLATEXs() { return saveFiles(LATEX);}


    public static File saveSQL() { return saveFile(SQL); }
    public static File[] saveSQLs() { return saveFiles(SQL);}

    public static File getDirectory(Stage stage) { return convert(dc.showDialog(stage))[0]; }

    public boolean hasNext() { return in.hasNext(); }

    public int count(String substring) {
        String input = read();
        String[] words = input.split(substring);
        return words.length-1;
    }

    public int count(char substring) { return count(substring+""); }

    public int count(int substring) { return count(substring+""); }

    public int count(double substring) { return count(substring+""); }

    public int count(float substring) { return count(substring+""); }

    public String[] split(String delimeter) {
        String input = read();
        String[] words = input.split(delimeter);
        return words;
    }

    public String read() {
        try {
            return new String(Files.readAllBytes(Paths.get(getAbsolutePath())));
        } catch (IOException e) {}
        return "";
    }

    public String readLine() {
        if(in != null) {
            if(hasNext()) return in.nextLine();
            else {
                try { Scanner inp = new Scanner(this); return inp.nextLine(); } catch (IOException e) {}
            }
        } return "";
    }

    public String[] readLines() {
        List<String> lines = Collections.emptyList();
        try { lines = Files.readAllLines(Paths.get(getAbsolutePath()), StandardCharsets.UTF_8); } catch (IOException e) {}
        return lines.toArray(new String[lines.size()]);
    }

    public void close() {
        try { in.close(); } catch(NullPointerException ex) {}
        try { out.close(); } catch(NullPointerException ex) {}
    }

    public String relativize(File file) { return toURI().relativize(file.toURI()).getPath(); }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if(obj == null || !(obj instanceof File)) return false;
        return getAbsolutePath().equals(((File) obj).getAbsolutePath());
    }

    public boolean equals(File other) { return getAbsolutePath().equals(other.getAbsolutePath()); }

    public File clone() { return new File(getAbsolutePath()); }

    public int compareTo(File o) { return super.compareTo(o); }
}