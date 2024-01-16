package parser;

public class TSymCour {

    private String nom;
    private CODES_LEX code;
    private CODES_LEX last_code ;
    private boolean newLine;
    

    public TSymCour() {
    }

    public TSymCour(CODES_LEX code, String nom) {
        this.code = code;
        this.nom = nom;
    }

    public boolean getNewLine(){
        return newLine;
    }

    public String getName(){
        return nom;
    }

    public CODES_LEX getCode() {
        return code;
    }

    public CODES_LEX getLastCode() {
        return last_code;
    }

    public void setName(String name){
        this.nom = name ;
    }

    public void setCode(CODES_LEX code) {
        this.last_code = this.code ;
        this.code = code;
    }

    public void setNewLine(boolean newLine){
        this.newLine = newLine;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setSymbole(String nom, CODES_LEX code){
        this.code = code;
        this.nom = nom;
    }
}
