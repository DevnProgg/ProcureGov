package com.ProcureGov.repository;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;

public class BaseRepository {
    protected DataSource getDataSource() throws Exception {
        Context ctx = new InitialContext();
        return (DataSource) ctx.lookup("java:comp/env/jdbc/procure_gov");
    }

    public Connection getConnection() throws Exception {
        return getDataSource().getConnection();
    }
}
