package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;

public class MakefileParser {
    private TSymCour SymboleCourant = new TSymCour();
    private BufferedReader reader;
    private String currentLine;
    private int currentIndex;
    private boolean erreur = false;
    private boolean start = false;
    private HashMap<Node, List<String>> graph ;
    private HashMap<Node, List<Node>> NewGraph ;
    private Node keyNode;
    private List<String> valueNodes ;

    public static void main(String[] args) {
        MakefileParser parser = new MakefileParser(); 
        HashMap<Node, List<Node>> result = parser.processFile("C:\\Users\\lenovo\\Desktop\\ENSIMAG\\SD\\Grid5000-distributed-system\\src\\scheduler\\Makefile");
        // HashMap<Node, List<Node>> result = parser.processFile("Makefile.txt");

    }

    public HashMap<Node, List<Node>> processFile(String filePath) {
        try  {
            reader = new BufferedReader(new FileReader(filePath));
            currentLine = reader.readLine();
            currentIndex = 0;
            graph = new HashMap<>();
            NewGraph = new HashMap<>();
            while (currentLine != null) {
                while (currentIndex <= currentLine.length()) {
                    NewGraph = PROGRAM();
                    if (currentLine == null) { break ;}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return NewGraph ;
    }

    private void Erreur(CODES_LEX code){
        erreur = true ;
        //System.out.println("Erreur dans le token : "+code);
    }

    private void Test_Symbole(CODES_LEX code){
        if (SymboleCourant.getCode() == code){
            nextToken();
        } else {
            Erreur(code);
            nextToken();
        }
    }

    public boolean checkFileExistence(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    private HashMap<Node, List<Node>> PROGRAM(){
        Rules();
        if (!erreur){
            System.out.println("Fin du makefile.");
        } else {
            throw new RuntimeException("Une erreur syntaxique s'est produite.");
        }

        StringBuilder concatenatedNodeNames = new StringBuilder();
        for (Node node : graph.keySet()) {
            concatenatedNodeNames.append(node.getNodeName());
        }

        //transformation des String en Node

        boolean isFile ;
        for (Map.Entry<Node, List<String>> entry : graph.entrySet()) {
            Node keyNode = entry.getKey();
            List<String> dependencies = entry.getValue();
            List<Node> dependencyNodes = new ArrayList<>();
            for (String dependency : dependencies) {
                isFile = true ;
                for (Map.Entry<Node, List<String>> entry2 : graph.entrySet()) {
                    Node keyNode2 = entry2.getKey();
                    if(keyNode2.getNodeName().equals(dependency)){
                        dependencyNodes.add(keyNode2);
                        isFile = false ;
                        break;
                    }
                }
                if(isFile == true){
                    Node Nodefile = new Node(dependency);
                    Nodefile.setIsFile(true);
                    dependencyNodes.add(Nodefile);
                }
            }
            NewGraph.put(keyNode, dependencyNodes);
        }

        //Affichage Graphe
        for (Map.Entry<Node, List<Node>> pairs : NewGraph.entrySet()) {
            Node keyNode = pairs.getKey();
            List<Node> valueNodes = pairs.getValue();
            System.out.println("keyNode : " + keyNode + " - List : "+ valueNodes);
        }

        //Vérification si tous les fichiers évoqué dans le MakeFile sont bien présent
        for (Map.Entry<Node, List<Node>> pairs : NewGraph.entrySet()) {
            List<Node> valueNodes = pairs.getValue();
            for (Node node : valueNodes){
                if(node.getIsFile()==true){
                    String filePathToCheck = node.getNodeName();
                    boolean fileExists = checkFileExistence("./"+filePathToCheck);
                    node.setStatus(TaskStatus.FINISHED);
                    if (!fileExists) {
                        throw new RuntimeException("Le fichier n'existe pas : " +  node.getNodeName());
                    }
                }
            }
        }

        return NewGraph ;
    }

    private void Rules(){
        Rule();
        if(currentLine != null) {
            Rules();
        }

    }

    private void Rule(){
        keyNode = new Node();
        valueNodes = new ArrayList<>();
        Target();
        Test_Symbole(CODES_LEX.TOKEN_COLON);
        Dependencies();
        Commandes();
        //System.out.println("keyNode : " + keyNode + " - List : "+ valueNodes);
        graph.put(keyNode, valueNodes);
    }

    private void Target(){
        if (!start) {
            nextToken();
            start = true ;
        }
        switch (SymboleCourant.getCode()){
            case TOKEN_ALL:
                //System.out.println("TOKEN ALL");
                keyNode.setNodeName(SymboleCourant.getName());
                nextToken();
                break;
            case TOKEN_TARGET :
                //System.out.println("TOKEN TARGET");
                keyNode.setNodeName(SymboleCourant.getName());
                nextToken();
                break;
            default :
                Erreur(CODES_LEX.TOKEN_TARGET);
                nextToken();
                break;
        }
    }

    private void Dependencies() {
        while (SymboleCourant.getCode() != CODES_LEX.TOKEN_TAB && SymboleCourant.getCode() != CODES_LEX.TOKEN_TARGET) {
            switch (SymboleCourant.getCode()) {
                case TOKEN_DEPENDENCIES:
                    //System.out.println("Dependency: " + SymboleCourant.getCode());
                    valueNodes.add(SymboleCourant.getName());
                    nextToken();
                    if (SymboleCourant.getCode() == CODES_LEX.TOKEN_COMMA) {
                        //System.out.println("Comma: " + SymboleCourant.getCode());
                        nextToken();
                        //Test_Symbole(CODES_LEX.TOKEN_DEPENDENCIES);
                    }
                    break;
                default:
                    nextToken();
                    if (currentLine == null ){ return ;}
                    break;
            }
        }
    }

    private void Commandes() {
        switch (SymboleCourant.getCode()) {
            case TOKEN_TAB :
                while (SymboleCourant.getCode() == CODES_LEX.TOKEN_TAB) {
                    nextToken();
                }
                //System.out.println("Command: " + SymboleCourant.getCode());
                nextToken();
                if (SymboleCourant.getCode() == CODES_LEX.TOKEN_TAB) {
                    Commandes();
                }
                break;
            case TOKEN_TARGET :
                break;
        }
    }

    private void nextToken() {
        try {
            if (currentLine != null) {
                if ( currentIndex >= currentLine.length()) {
                    currentLine = reader.readLine();
                    SymboleCourant.setNewLine(true);
                    currentIndex = 0;
                    if (currentLine == null) {
                        return;
                    }
                }
            } else {return ;}

            char character = currentLine.charAt(currentIndex);
            while (character == ' ') {
                currentIndex++;
                if (currentIndex >= currentLine.length()) {
                    return;
                }
                character = currentLine.charAt(currentIndex);
            }
            Result result = processToken(character, currentLine, currentIndex);
            //System.out.println(SymboleCourant.getCode());

            //System.out.println("result : "+result.nextLine);
            //System.out.println("The line here : "+currentLine);
            if (result.nextLine) {
                SymboleCourant.setNewLine(true);
                String currentLine2 = reader.readLine();
                if (currentLine2 != null) {
                    // System.out.println("saut de ligne");
                    currentLine = currentLine2;
                    currentIndex = 0;
                } else {
                    currentLine = null ;
                }
            } else {
                SymboleCourant.setNewLine(false);
                //System.out.println("Pas de saut de ligne");
                if(result.changeI != 0){
                    currentIndex = result.changeI;
                } else {
                    currentIndex ++ ;
                }
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Result processToken(char character, String line, int index) {
        switch (character) {
            case '#':
                //System.out.println(character + " = Commentaire");
                SymboleCourant.setCode(CODES_LEX.TOKEN_COMMENT);
                //index-- ;
                return new Result(true, true, index);
            case ':':
                //System.out.println(character + " = COLON");
                SymboleCourant.setCode(CODES_LEX.TOKEN_COLON);
                break;
            case ',':
                //System.out.println(character + " = Comma");
                SymboleCourant.setCode(CODES_LEX.TOKEN_COMMA);
                break;
            case ';':
                //System.out.println(character + " = SemiColon");
                SymboleCourant.setCode(CODES_LEX.TOKEN_SMCOLON);
                break;
            case '\t':
                //System.out.println(character + " = Tabulation");
                SymboleCourant.setCode(CODES_LEX.TOKEN_TAB);
                break;
            default:
                String variable = "" ;
                while (character != ':' && character != ' ' && character != ','   ) {
                    variable = String.format("%s%s", variable, character);
                    index++;
                    if (index >= line.length()) {
                        break;
                    }
                    character = line.charAt(index);
                }

                //System.out.println("variable : " + variable);


                //Si le dernier symbole est une tabulation alors il s'agit d'une commande
                if (SymboleCourant.getCode() == CODES_LEX.TOKEN_TAB) {
                    SymboleCourant.setCode(CODES_LEX.TOKEN_COMMAND);
                    String resultString = currentLine.replaceFirst("^\\t+", "");
                    keyNode.addCommand(resultString);
                    return new Result(true, true, index);
                } else {
                    //il s'agit donc soit d'une cible ou d'une dependance
                    //si le symbole précédant est un COLON (:) alors c'est une dépendance
                    if (SymboleCourant.getCode() == CODES_LEX.TOKEN_COLON && SymboleCourant.getNewLine() == false ||
                            (SymboleCourant.getCode() == CODES_LEX.TOKEN_COMMA && SymboleCourant.getLastCode() == CODES_LEX.TOKEN_DEPENDENCIES) ||
                            SymboleCourant.getCode() == CODES_LEX.TOKEN_DEPENDENCIES && SymboleCourant.getNewLine() == false
                    ) {
                        SymboleCourant.setCode(CODES_LEX.TOKEN_DEPENDENCIES);
                        SymboleCourant.setName(variable);
                    }
                    else {
                        //si le symbole suivant est un COLON (:) alors il s'agit d'une cible
                        int index2 = index ;
                        boolean isColon = false ;
                        while (character != ':') {
                            //while (character != ':' && character != ' ') {
                            index2++;
                            if (index2 >= line.length()) {
                                break;
                            }
                            character = line.charAt(index2);
                        }
                        if (index2 < line.length()) {
                            isColon = true ;
                        }

                        if(isColon){
                            if(variable.equals("all")){
                                SymboleCourant.setCode(CODES_LEX.TOKEN_ALL);
                                SymboleCourant.setName("all");
                            } else {
                                SymboleCourant.setCode(CODES_LEX.TOKEN_TARGET);
                                SymboleCourant.setName(variable);
                            }
                        } else {
                            SymboleCourant.setCode(CODES_LEX.TOKEN_VAR);
                        }
                    }


                    //character = line.charAt(index);
                    return new Result(true, false, index);
                }
        }
        return new Result(false, false, 0);
    }

    // Classe pour gérer les résultats du traitement de caractère
    private static class Result {
        boolean shouldSkip;
        boolean nextLine;
        int changeI;

        Result(boolean shouldSkip, boolean nextLine, int changeI) {
            this.shouldSkip = shouldSkip;
            this.nextLine = nextLine;
            this.changeI = changeI;
        }
    }
}