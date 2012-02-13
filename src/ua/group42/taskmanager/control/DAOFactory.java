package ua.group42.taskmanager.control;

import java.io.IOException;
import java.lang.reflect.Constructor;
import org.apache.log4j.*;
import ua.group42.taskmanager.control.ConfigReader.ResType;
import ua.group42.taskmanager.control.data.TaskDAO;

/**
 * Abstract-Factory of DataAccessObject
 * @author Group42
 */
public class DAOFactory {

    private static final Logger log = Logger.getLogger(DAOFactory.class);

    /**
     * Return choosed by config settings DAO
     * @param confReader contains choice of DAO
     * @return DAO
     * @throws BadConfigException if error with choice occured 
     * (#see ua.group42.taskmanager.control.ConfigReader.ResType)
     */
    static TaskDAO getDAO(ConfigReader confReader) throws BadConfigException, IOException {

        try {
            ResType resType = confReader.getResourcesType();
            // TODO: make reflection here
            switch (resType) {
                case CSV:
                    Class csv = Class.forName("ua.group42.taskmanager.control.data.CsvDAO");
                    Constructor csvConstr = csv.getConstructor(ConfigReader.class);
                    TaskDAO daoCsv = (TaskDAO) csvConstr.newInstance(confReader);
                    return daoCsv;
                case XML:
                    Class xml = Class.forName("ua.group42.taskmanager.control.data.XmlDAO");
                    Constructor xmlConstr = xml.getConstructor(ConfigReader.class);
                    TaskDAO daoXml = (TaskDAO) xmlConstr.newInstance(confReader);
                    return daoXml;
                case DB:
                default:
                    throw new BadConfigException(confReader);
            }
        } catch (Exception ex) {
            log.fatal("Error in DAO Factory occured", ex);
            throw new RuntimeException("Error in DAO Factory occured", ex);
        }
    }
}
