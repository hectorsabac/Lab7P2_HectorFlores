package Lab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author hecto
 */
public class Steam {
    
    
    RandomAccessFile codes, games, players;
    
    public Steam(){
        try{
            File mifile = new File("steam");
            mifile.mkdir();
            
            //Falta poner este folder dentro del de steam
            File mifile1 = new File("steam/downloads");
            mifile1.mkdir();
            
            codes = new RandomAccessFile("steam/downloads/codes.stm ","rw");
            games = new RandomAccessFile("steam/downloads/games.stm","rw");
            players = new RandomAccessFile("steam/downloads/player.stm","rw");
            
            initCodes();
        }catch(IOException e){
            System.out.println("Error");
        }
    }
    
    /*
    Formato codes.stm
    
    int nuevos juegos
    int nuevos clientes
    int nuevos downloads
    */
    
    private void initCodes()throws IOException {
        //Pone el puntero en el inicio del archivo (por cualquier cosa)
        codes.seek(0);
        //Ingresa 3 1s en el archivo codes.stm si este esta vacio
        if(codes.length() == 0){
            codes.writeInt(1);
            codes.writeInt(1);
            codes.writeInt(1);
        }
    }
    
    
    /*
    Formato players.stm:
    
    int code
    String username
    String password
    String nombre
    long nacimiento
    int downloads
    String imageName
    String tipo de usuario
    */
    
    public void addPlayer(String user, int age, String userType) throws IOException {
            players.seek(players.length());
            int code = getCode(1);
    
            
            players.writeInt(code);
            players.writeUTF(user);
            players.writeUTF("");
            players.writeUTF("");
            players.writeLong(0);
            players.writeInt(0);
            players.writeUTF("");
            
            players.writeUTF(userType);
}
    
    /*
    Byte 0: Games
    Byte 1: Clientes
    Byte 2: Downloads
    */
    
    private int getCode(int Byte)throws IOException{
        //Posiciona el puntero en el byte que queremos accesar
        codes.seek(Byte);     
        //Extrae el codigo de ese byte
        int code = codes.readInt();
        //Vuelve a posicionar el puntero en ese byte
        codes.seek(Byte);
        //Escribe el nuevo codigo en ese byte
        codes.writeInt(code + 1);
        //Retorna el codigo anterior
        return code;
    }
    
    /*
    Formato para games.stm:
    int code
    String titulo
    char os (Sistema operativo)
    int edadMinima
    double precio
    int contador de downloads
    String imageName
    */
    
    public void addGame(String titulo, char os, int minAge, double precio, String imageName) throws IOException{
        //Extrae el code que se va a asignar y se escribe el nuevo code en codes.stm
        int code = getCode(0);
        
        //Posiciona el puntero de games al final del archivo
        games.seek(games.length());
        //Escribe el code de game (su id)
        games.write(code);
        //Escribe el titulo del juego
        games.writeUTF(titulo);
        //Escribe el sistema operativo de este
        games.writeChar(os);
        //Escribe la edad minima de este
        games.writeInt(minAge);
        //Escribe el precio
        games.writeDouble(precio);
        //Escribe downloads: default en 0
        games.writeInt(0);
        //Escribe el nombre de la imagen
        games.writeUTF(imageName);
    }
    
    /*
    Formato players.stm:
    
    int code
    String username
    String password
    String nombre
    long nacimiento
    int downloads
    String imageName
    String tipo de usuario
    */
    
    public String buscarCliente(int code) throws IOException{
        //Posiciona el puntero al principio del archivo players.stm
        players.seek(0);
        
        //Recorrido de archivo
        while (players.getFilePointer() < players.length()){
            //Extrae el codigo del jugador
            int playerCode = (int) players.readInt();
            players.readUTF();
            players.readUTF();
            String playerName = (String) players.readUTF();
            
            if (playerCode == code){
                return playerName;
            }
            
            players.skipBytes(12); //Skip long(8 bytes)+ int (4 bytes) = 12 bytes
            
            players.readUTF();
            players.readUTF();
        }
        
        return null;
    }
    
    /* 
    Formato para games.stm:
    
    int code
    String titulo
    char os (Sistema operativo)
    int edadMinima
    double precio
    int contador de downloads
    String imageName
    */
    
    public String buscarGame (int code) throws IOException{
        //Puntero al inicio del archivo
        games.seek(0);
        
        //Recorrido de archvio
        while (games.getFilePointer() < games.length()){
            int gameCode = (int) games.readInt();
            String gameTitle = (String) games.readUTF();
            
            if (gameCode == code){
                return gameTitle;
            }
            
            //Skip char (2 bytes) + int (4 bytes) + double (8 bytes) + int (4 bytes) = 18 bytes
            games.skipBytes(18);
            games.readUTF();
        }
        
        return null;
    }
    
    public double buscarPrice (int code) throws IOException{
        //Puntero al inicio del archivo
        games.seek(0);
        
        //Recorrido de archvio
        while (games.getFilePointer() < games.length()){
            int gameCode = (int) games.readInt();
            games.readUTF();
            
            //Skip char (2 bytes) + int (4 bytes) = 6 bytes
            games.skipBytes(6);
            
            double price = (Double) games.readDouble();
            
            if(gameCode == code){
                return price;
            }
            
            games.skipBytes(4);
            games.readUTF();
        }
        
        return 0;
    }
    
    /* 
    Formato para games.stm:
    
    int code
    String titulo
    char os (Sistema operativo)
    int edadMinima
    double precio
    int contador de downloads
    String imageName
    */
    
    public boolean isAvailableForOS (int code, char os) throws IOException{
        //Puntero al inicio del archivo
        games.seek(0);
        
        //Recorrido de archvio
        while (games.getFilePointer() < games.length()){
            int gameCode = (int) games.readInt();
            games.readUTF();
            
            //Skip char (2 bytes) 
            
            char operatingSystem = (char) games.readChar();
            
            //Skip int (4 bytes)
            games.skipBytes(4);
            
            if(gameCode == code && os == operatingSystem){
                return true;
            }
            
            //Skip double (8 bytes) + int (4 bytes) = 12 bytes
            games.skipBytes(12);
            games.readUTF();
        }
        
        return false;
    }
    
    private int buscarEdadMinima (int code) throws IOException {
        //Puntero al inicio del archivo
        games.seek(0);
        
        //Recorrido de archvio
        while (games.getFilePointer() < games.length()){
            int gameCode = (int) games.readInt();
            games.readUTF();
            
            //Skip char (2 bytes) 
            games.skipBytes(2);
            
            int edadMinima = (int) games.readInt();
            
            if(gameCode == code){
                return edadMinima;
            }
            
            //Skip double (8 bytes) + int (4 bytes) = 12 bytes
            games.skipBytes(12);
            games.readUTF();
        }
        
        return 0;
    }
    
    private boolean verifyAge (int code)throws IOException{ 
        Calendar nacimientoEnCalendar = Calendar.getInstance();
        
        players.seek(0); 
        while(players.getFilePointer()<players.length()){ 
            int codigo = players.readInt(); 
            players.readUTF(); 
            players.readUTF(); 
            players.readUTF(); 
            long nacimiento = players.readLong(); 
            players.readInt(); 
            players.readUTF(); 
            players.readUTF(); 
            
            if(codigo == code){ 
                nacimientoEnCalendar.setTimeInMillis(nacimiento); 
            }
        }
        
        nacimientoEnCalendar.add(Calendar.YEAR, buscarEdadMinima(code));
        
        if (nacimientoEnCalendar.before(Calendar.getInstance())){
            return true;
        }
        
        return false;
        
    }
    
    public boolean existePlayerCode(int code)throws IOException{
        players.seek(0);
        while(players.getFilePointer()<players.length()){
            int codigo=players.readInt();
            players.readUTF();
            players.readUTF();
            players.readLong();
            players.readInt();
            players.readUTF();
            players.readUTF();
            if(codigo==code){
                return true;
            }
        }
        return false;
    }
    
    public boolean existeGameCode(int code)throws IOException{
        games.seek(0);
        while(games.getFilePointer()<games.length()){
            int codigo=games.read();
            games.readUTF();
            games.readChar();
            games.readInt();
            games.readDouble();
            games.readInt();
            games.readUTF();
            if(codigo==code){
                return true;
            }
        }
        return false;
    }
    
    SimpleDateFormat simple = new SimpleDateFormat("dd-MM-yy");
    
    public boolean downloadGame (int gameCode, int playerCode, char os) throws IOException{
        //Posiciona el puntero de codes en el byte que contiene el codigo de juego
        codes.seek(0);
        if (existeGameCode(gameCode) && existePlayerCode(playerCode) && verifyAge(playerCode) && isAvailableForOS(gameCode, os)){
            //Genera el path de este nuevo archivo
            int downloadCode = getCode(3);
            String newFilePath = "steam/downloads/download_" + downloadCode + ".stm";
            
            
            //Crea el archivo
            RandomAccessFile nuevoFile = new RandomAccessFile(newFilePath, "rw");
            
            //Escribe la fecha actual en formato dd-MM-yy comoun string
            nuevoFile.writeUTF(simple.format(Calendar.getInstance()));
            
            JOptionPane.showMessageDialog(null, "Download #" + downloadCode + "\n" + buscarCliente(playerCode) + " has bajado " + buscarGame(gameCode) +
                    " a un precio de: " + buscarPrice(gameCode));
            
            return true;
        }
        
        return false;
    }
    
    public void updatePriceFor(int code, double price)throws IOException{ 
        games.seek(0); 
        while(games.getFilePointer()<games.length()){ 
            int codigo=games.read(); 
            games.readUTF(); 
            games.readChar(); 
            games.readInt();
            
            if(codigo==code){ 
                games.writeDouble(price); 
            } else {
                games.skipBytes(8);
            }
            
            games.readInt(); 
            games.readUTF(); 
        } 
    }
    
    /*
    Formato players.stm:
    
    int code
    String username
    String password
    String nombre
    long nacimiento
    int downloads
    String imageName
    String tipo de usuario
    */
    
    public void reportForClient(int codeclient, String txtFile)throws IOException{ 
        players.seek(0); 
        boolean creoReporte=false; 
        while(players.getFilePointer()<players.length()){ 
            int codigo=players.readInt(); 
            String user=players.readUTF(); 
            String password=players.readUTF(); 
            String nombre=players.readUTF(); 
            long nacimiento=players.readLong(); 
            
            Calendar nacimientoEnCalendar = Calendar.getInstance();
            nacimientoEnCalendar.setTimeInMillis(nacimiento);
            
            String nacimientoSimple = simple.format(nacimientoEnCalendar.getTime());
            
            int contadorDeDownloads=players.readInt(); 
            String imagen=players.readUTF(); 
            String tipoDeUser=players.readUTF(); 
            if(codigo==codeclient){ 
                FileWriter file=new FileWriter(txtFile+".txt", false); 
                file.write("Codigo: "+codigo); 
                file.write("Usuario: "+user); 
                file.write("Contraseña: "+password); 
                file.write("Nombre de usuario: "+nombre); 
                file.write("Fecha de nacimiento: "+nacimientoSimple); 
                file.write("Cuantos juegos a descargado: "+contadorDeDownloads); 
                file.write("Imagen de usuario: "+imagen); 
                file.write("Tipo de usuario: "+tipoDeUser); 
                creoReporte=true; 
                JOptionPane.showMessageDialog(null, "REPORTE CREADO"); 
            } 
        } 
        if(creoReporte==false){ 
            JOptionPane.showMessageDialog(null, "NO SE PUEDE CREAR REPORTE"); 
        } 
    }

    /* 
    Formato para games.stm:
    
    int code
    String titulo
    char os (Sistema operativo)
    int edadMinima
    double precio
    int contador de downloads
    String imageName
    */
    
    JList gameList=new JList(); 
    DefaultListModel modelo=new DefaultListModel(); 
    public void printGames()throws IOException{ 
        modelo.clear(); 
        games.seek(0); 
        while(games.getFilePointer()<games.length()){ 
            int codigo=games.read(); 
            String titulo=games.readUTF(); 
            char os=games.readChar(); 
            int minimoEdad=games.readInt(); 
            double precio=games.readDouble(); 
            int descargas=games.readInt(); 
            String imagen=games.readUTF(); 
            modelo.addElement("Codigo: "+codigo+", OS: "+os+ 
            ", Titulo: "+titulo+", Minimo de edad para jugar: "+minimoEdad+ 
            ", Precio: $"+precio+", Cuantas veces a sido descargado: "+descargas+", Imagen de juego: "+imagen); 
            gameList.setModel(modelo); 
        } 
    }

public void createUserDirectory(String username, int age, String userType) {
        // Directorio principal donde se almacenarán los usuarios
        File usersDirectory = new File("steam/users");
        
        // Crear el directorio de usuarios si no existe
        if (!usersDirectory.exists()) {
            usersDirectory.mkdirs();
        }
        
        // Crear el directorio del usuario específico
        File userDirectory = new File(usersDirectory, username);
        
        // Crear el directorio del usuario
        if (!userDirectory.exists()) {
            userDirectory.mkdirs();
        }
        
        // Crear y escribir el archivo de información del usuario
        File userInfoFile = new File(userDirectory, "info.txt");
        try {
            userInfoFile.createNewFile();
            java.io.FileWriter fileWriter = new java.io.FileWriter(userInfoFile);
            fileWriter.write("Username: " + username + "\n");
            fileWriter.write("Age: " + age + "\n");
            fileWriter.write("User Type: " + userType + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public boolean login(String username) {
        try {
            players.seek(0);

            while (players.getFilePointer() < players.length()) {
                int code = players.readInt();
                String user = players.readUTF();
                String pass = players.readUTF();
                if (user.equals(username)) {
                    return true;
                }
                players.skipBytes(28);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
        
        public void modifyPlayer(String username, int age, String userType) throws IOException {
    // Posiciona el puntero al principio del archivo
    players.seek(0);
    
    // Recorrido del archivo para encontrar al jugador por su nombre de usuario
    while (players.getFilePointer() < players.length()) {
        int code = players.readInt(); // Lee el código del jugador
        String user = players.readUTF(); // Lee el nombre de usuario
        if (user.equals(username)) { // Verifica si el nombre de usuario coincide
            // Actualiza la información del jugador con los nuevos datos
            players.writeUTF(username); // Escribe el nuevo nombre de usuario
            players.writeInt(age); // Escribe la nueva edad
            players.writeUTF(userType); // Escribe el nuevo tipo de usuario
            JOptionPane.showMessageDialog(null, "¡Jugador modificado exitosamente!");
            return; // Termina el método después de encontrar y modificar al jugador
        }
        // Si el nombre de usuario no coincide, salta al siguiente registro de jugador
        players.skipBytes(20); // Saltar la longitud del password (UTF-8) + longitud del nombre (UTF-8) + edad (int) = 20 bytes
        players.readLong(); 
        players.readInt(); 
        players.readUTF(); 
        players.readUTF(); 
    }
    

    
}
    
}
