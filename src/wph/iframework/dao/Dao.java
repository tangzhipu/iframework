package wph.iframework.dao;

import wph.iframework.dao.db.Database;

import common.Logger;

/**
 * 
 * @author WPH
 * 
 */
public abstract class Dao
{
    protected Database            db     = null;
    
    protected final static Logger logger = Logger.getLogger(Dao.class);
    
    public Dao(Database db)
    {
        this.db = db;
    }
}
