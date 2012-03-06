package ua.group42.taskmanager.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.text.ParseException;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import ua.group42.taskmanager.control.ControllerIface;
import ua.group42.taskmanager.protocol.*;

/**
 *
 * @author Silvan
 */
public class ServWorker implements Runnable {
    
    private static final Logger log = Logger.getLogger(ServWorker.class);
    
    private Socket clientSocket = null;
    private ServerSideWrapperIface server = null;
    private ControllerIface taskController = null;

    public ServWorker(ServerSideWrapperIface server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
    }
    
    private boolean logIn(String login) throws IllegalAccessException {
        
        if (server.canConnect(login)) {
            server.getUser(login).setState(UserModel.State.ONLINE);
            return true;
        }
        
        return false;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        StringBuilder message = null;
        String readed;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            do {
                message = new StringBuilder();
              do {
                 // TODO: implement logic here
                 
                 readed = reader.readLine();
                 message.append(readed);
                 log.info(readed);
              } while ( !(readed.equalsIgnoreCase("</methodCall>") || readed.equalsIgnoreCase("</methodResponse>")));
              
              String command = message.toString();
              SAXBuilder builder = new SAXBuilder();
              Document doc = builder.build(readed);
              
              MessageParser msg = new MessageParser(doc);
              if (msg.isRequest()) {
                    try {
                        RequestParser request = new RequestParser(msg);
                        
//                        Class c = Class.forName("ServWorker");
//                        Method[] allMethods = c.getMethods();
//                        for (Method m : allMethods) {
//                            if (m.getName().equals("run"))
//                                continue;
//                            if (m.getName().equals(request.getMethodName()))
//                                m.invoke(this, request.getParametr());
//                            
//                        }
                      try {  
                        if (request.getMethodName().equals("logIn")) {
                            boolean result = logIn((String)request.getParametr());
                            if (result == true)
                                writer.write(NetProtocol.ServerSide.responseOK());
                            else 
                                writer.write(NetProtocol.ServerSide.responseError(NetProtocol.WrongLoginNameError, (String) request.getParametr()));
                        }
                      } catch (IllegalAccessException ex) {
                        log.error("Cannot access with given userName :" + request.getParametr());
                    }
                      
                    } catch (ParseException ex) {
                        log.error("Command parsing error.", ex);
                    } 
              }
              
            } while (!clientSocket.isClosed());
             log.info("Closing stream.");
        } catch (IOException ex) {
            log.error("socket i/o error occured",ex);
        } catch (JDOMException ex) {
            log.error("Parsing message error.", ex);
//        } catch (ClassNotFoundException impossible) {
//            log.error("Class wasn't found", impossible);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                    log.info ("reader closed");
                }
                if (writer != null) {
                    writer.close();
                    log.info ("writer closed");
                }
                if (clientSocket != null) {
                    clientSocket.close();
                    log.info("connection closed");
                }
            } catch (IOException ex) {
                log.error("Closing streams error", ex);
            }
        }
    }
    
}
