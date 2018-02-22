/*
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2013 JSQLParser
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.sf.jsqlparser.expression;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import net.sf.jsqlparser.parser.ASTNodeAccessImpl;

/**
 * A Timestamp in the form {ts 'yyyy-mm-dd hh:mm:ss.f . . .'}
 */
public class TimestampValue extends ASTNodeAccessImpl implements Expression {

    private Timestamp value;
    private char quotation = '\'';
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd[[ ]['T']HH:mm[:ss][.SSS][.SS]]")
                    .withZone(ZoneId.systemDefault())
                    .withLocale(Locale.getDefault());

    public TimestampValue(String value) {
        if (value == null) {
            throw new java.lang.IllegalArgumentException("null string");
        } else {
            try {
                if (value.charAt(0) == quotation) {
                    this.value = toTimestamp(value.substring(1, value.length() - 1));
                } else {
                    this.value = toTimestamp(value.substring(0, value.length()));
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException(
                        "Could not parse " + value + " with format " + FORMATTER.toString());
            }
        }
    }

    private Timestamp toTimestamp(String token) throws ParseException {
        return Timestamp.from(
                Instant.from(
                        FORMATTER.parseBest(token, ZonedDateTime::from, LocalDateTime::from, LocalDate::from)));
    }

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    public Timestamp getValue() {
        return value;
    }

    public void setValue(Timestamp d) {
        value = d;
    }

    @Override
    public String toString() {
        return "{ts '" + value + "'}";
    }
}
