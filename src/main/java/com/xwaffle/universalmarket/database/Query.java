package com.xwaffle.universalmarket.database;

/**
 * Created by Dr. Nick Doran on 7/23/2016.
 */
public class Query {

    private String query;

    public Query() {
        query = "";
    }

    public Query And() {
        query += "AND" + " ";
        return this;
    }

    public Query Duplicate() {
        query += "ON DUPLICATE KEY" + " ";
        return this;
    }

    public Query Parenthesis(Object... obj) {
        query += "(";
        for (Object o : obj) {
            query += o + ",";
        }
        query = query.endsWith(",") ? query.substring(0, query.lastIndexOf(",")) : query;
        query += ")" + " ";
        return this;
    }

    public Query Values(Object... obj) {
        query += "VALUES" + " ";
        query += "(";
        for (Object o : obj) {
            if (o instanceof String) {
                query += "'" + o + "'" + ",";
            } else {
                query += o + ",";
            }
        }
        query = query.endsWith(",") ? query.substring(0, query.lastIndexOf(",")) : query;
        query += ")" + " ";
        return this;
    }

    public Query Set() {
        query += "SET" + " ";
        return this;
    }

    public Query Update() {
        query += "UPDATE" + " ";
        return this;
    }

    public Query Insert() {
        query += "INSERT" + " ";
        return this;
    }

    public Query Into() {
        query += "INTO" + " ";
        return this;
    }

    public Query Select() {
        query += "SELECT" + " ";
        return this;
    }

    public Query All() {
        query += "*" + " ";
        return this;
    }

    public Query Where() {
        query += "WHERE" + " ";
        return this;
    }

    public Query From() {
        query += "FROM" + " ";
        return this;
    }

    public Query Delete() {
        query += "DELETE" + " ";
        return this;
    }

    public Query Table(String par1) {
        query += "`" + par1 + "`" + " ";
        return this;
    }

    public Query Field(Object par1) {
        query += par1 + " ";
        return this;
    }

    public Query Fields(Object... obj) {
        for (Object o : obj) {
            query += o + ",";
        }
        query = query.endsWith(",") ? query.substring(0, query.lastIndexOf(",")) : query;
        query += " ";
        return this;
    }

    public Query asString(String par1) {
        if (par1.startsWith("'")) {
            query += par1;
            if (par1.endsWith("'")) {
                query += " ";
            } else {
                query += "'" + " ";
            }
            return this;
        } else {
            query += "'" + par1 + "'" + " ";
            return this;
        }
    }

    public Query asLong(long par1) {
        query += par1 + " ";
        return this;
    }

    public Query asInt(int par1) {
        query += par1 + " ";
        return this;
    }

    public Query asDouble(double par1) {
        query += par1 + " ";
        return this;
    }

    public Query Equals() {
        query += "=" + " ";
        return this;
    }

    public Query End() {
        query += ";";
        return this;
    }

    public Query Comma() {
        query += ", ";
        return this;
    }

    public String getQuery() {
        return query.trim();
    }

}
