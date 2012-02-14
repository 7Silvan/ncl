package ua.group42.taskmanager.control;

import ua.group42.taskmanager.model.InternalControllerException;
import java.lang.reflect.Constructor;
import org.apache.log4j.*;
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
    static TaskDAO getDAO(ConfigReader confReader) throws BadConfigException {

        try {
            Class daoClass = Class.forName(confReader.getDaoClassName());
            Constructor daoConstruct = daoClass.getConstructor(ConfigReader.class);
            TaskDAO dao = (TaskDAO) daoConstruct.newInstance(confReader);
            return dao;
        } catch (Exception ex) {
            log.fatal("Error DAO Initializing  occured", ex);
            throw new InternalControllerException("Error DAO Initializing occured", ex.getCause());
        }
    }
}
