/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Tee.java
 * Copyright (C) 2005-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.core;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

/**
 * This class pipelines print/println's to several PrintStreams. Useful for
 * redirecting System.out and System.err to files etc.<br/>
 * E.g., for redirecting stderr/stdout to files with timestamps and:<br/>
 * 
 * <pre>
 *    import java.io.*;
 *    import weka.core.Tee;
 *
 *    ...
 *    // stdout
 *    Tee teeOut = new Tee(System.out);
 *    teeOut.add(new PrintStream(new FileOutputStream("out.txt")), true);
 *    System.setOut(teeOut);
 *
 *    // stderr
 *    Tee teeErr = new Tee(System.err);
 *    teeErr.add(new PrintStream(new FileOutputStream("err.txt")), true);
 *    System.setOut(teeErr);
 *    ...
 * </pre>
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

public class Tee extends PrintStream implements RevisionHandler {

    /** the different PrintStreams. */
    protected Vector<PrintStream> m_Streams = new Vector<PrintStream>();

    /** whether to add timestamps or not. */
    protected Vector<Boolean> m_Timestamps = new Vector<Boolean>();

    /** whether to add a prefix or not. */
    protected Vector<String> m_Prefixes = new Vector<String>();

    /** the default printstream. */
    protected PrintStream m_Default = null;

    /**
     * initializes the object, with a default printstream.
     */
    public Tee() {
        this(System.out);
    }

    /**
     * initializes the object with the given default printstream, e.g., System.out.
     *
     * @param def the default printstream, remains also after calling clear()
     */
    public Tee(PrintStream def) {
        super(def);

        m_Default = def;
        clear();
    }

    /**
     * removes all streams and places the default printstream, if any, again in the
     * list.
     *
     * @see #getDefault()
     */
    public void clear() {
        m_Streams.clear();
        m_Timestamps.clear();
        m_Prefixes.clear();

        if (getDefault() != null)
            add(getDefault());
    }

    /**
     * returns the default printstrean, can be NULL.
     *
     * @return the default printstream
     * @see #m_Default
     */
    public PrintStream getDefault() {
        return m_Default;
    }

    /**
     * adds the given PrintStream to the list of streams, with NO timestamp and NO
     * prefix.
     *
     * @param p the printstream to add
     */
    public void add(PrintStream p) {
        add(p, false);
    }

    /**
     * adds the given PrintStream to the list of streams, with NO prefix.
     *
     * @param p         the printstream to add
     * @param timestamp whether to use timestamps or not
     */
    public void add(PrintStream p, boolean timestamp) {
        add(p, timestamp, "");
    }

    /**
     * adds the given PrintStream to the list of streams.
     *
     * @param p         the printstream to add
     * @param timestamp whether to use timestamps or not
     * @param prefix    the prefix to use
     */
    public void add(PrintStream p, boolean timestamp, String prefix) {
        if (m_Streams.contains(p))
            remove(p);

        // make sure it's not null
        if (prefix == null)
            prefix = "";

        m_Streams.add(p);
        m_Timestamps.add(new Boolean(timestamp));
        m_Prefixes.add(prefix);
    }

    /**
     * returns the specified PrintStream from the list.
     *
     * @param index the index of the PrintStream to return
     * @return the specified PrintStream, or null if invalid index
     */
    public PrintStream get(int index) {
        if ((index >= 0) && (index < size()))
            return (PrintStream) m_Streams.get(index);
        else
            return null;
    }

    /**
     * removes the given PrintStream from the list.
     *
     * @param p the PrintStream to remove
     * @return returns the removed PrintStream if it could be removed, null
     *         otherwise
     */
    public PrintStream remove(PrintStream p) {

        int index;
        if ((index = m_Streams.indexOf(p)) != -1) {
            m_Timestamps.remove(index);
            m_Prefixes.remove(index);
            return (PrintStream) m_Streams.remove(index);
        } else {
            return null;
        }
    }

    /**
     * removes the given PrintStream from the list.
     *
     * @param index the index of the PrintStream to remove
     * @return returns the removed PrintStream if it could be removed, null
     *         otherwise
     */
    public PrintStream remove(int index) {
        if ((index >= 0) && (index < size())) {
            m_Timestamps.remove(index);
            m_Prefixes.remove(index);
            return (PrintStream) m_Streams.remove(index);
        } else {
            return null;
        }
    }

    /**
     * checks whether the given PrintStream is already in the list.
     *
     * @param p the PrintStream to look for
     * @return true if the PrintStream is in the list
     */
    public boolean contains(PrintStream p) {
        return m_Streams.contains(p);
    }

    /**
     * returns the number of streams currently in the list.
     *
     * @return the number of streams in the list
     */
    public int size() {
        return m_Streams.size();
    }

    /**
     * prints the prefix/timestamp (timestampe only to those streams that want one).
     */
    private void printHeader() {
        for (int i = 0; i < size(); i++) {
            // prefix
            if (!((String) m_Prefixes.get(i)).equals(""))
                ((PrintStream) m_Streams.get(i)).print("[" + m_Prefixes.get(i) + "]\t");

            // timestamp
            if (((Boolean) m_Timestamps.get(i)).booleanValue())
                ((PrintStream) m_Streams.get(i)).print("[" + new Date() + "]\t");
        }
    }

    /**
     * flushes all the printstreams.
     */
    public void flush() {
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).flush();
    }

    /**
     * prints the given int to the streams.
     *
     * @param x the object to print
     */
    public void print(int x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).print(x);
        flush();
    }

    /**
     * prints the given long to the streams.
     *
     * @param x the object to print
     */
    public void print(long x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).print(x);
        flush();
    }

    /**
     * prints the given float to the streams.
     *
     * @param x the object to print
     */
    public void print(float x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).print(x);
        flush();
    }

    /**
     * prints the given double to the streams.
     *
     * @param x the object to print
     */
    public void print(double x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).print(x);
        flush();
    }

    /**
     * prints the given boolean to the streams.
     *
     * @param x the object to print
     */
    public void print(boolean x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).print(x);
        flush();
    }

    /**
     * prints the given char to the streams.
     *
     * @param x the object to print
     */
    public void print(char x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).print(x);
        flush();
    }

    /**
     * prints the given char array to the streams.
     *
     * @param x the object to print
     */
    public void print(char[] x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).print(x);
        flush();
    }

    /**
     * prints the given string to the streams.
     *
     * @param x the object to print
     */
    public void print(String x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).print(x);
        flush();
    }

    /**
     * prints the given object to the streams.
     *
     * @param x the object to print
     */
    public void print(Object x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).print(x);
        flush();
    }

    /**
     * prints a new line to the streams.
     */
    public void println() {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).println();
        flush();
    }

    /**
     * prints the given int to the streams.
     *
     * @param x the object to print
     */
    public void println(int x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).println(x);
        flush();
    }

    /**
     * prints the given long to the streams.
     *
     * @param x the object to print
     */
    public void println(long x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).println(x);
        flush();
    }

    /**
     * prints the given float to the streams.
     *
     * @param x the object to print
     */
    public void println(float x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).println(x);
        flush();
    }

    /**
     * prints the given double to the streams.
     *
     * @param x the object to print
     */
    public void println(double x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).println(x);
        flush();
    }

    /**
     * prints the given boolean to the streams.
     *
     * @param x the object to print
     */
    public void println(boolean x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).println(x);
        flush();
    }

    /**
     * prints the given char to the streams.
     *
     * @param x the object to print
     */
    public void println(char x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).println(x);
        flush();
    }

    /**
     * prints the given char array to the streams.
     *
     * @param x the object to print
     */
    public void println(char[] x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).println(x);
        flush();
    }

    /**
     * prints the given string to the streams.
     *
     * @param x the object to print
     */
    public void println(String x) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).println(x);
        flush();
    }

    /**
     * prints the given object to the streams (for Throwables we print the stack
     * trace).
     *
     * @param x the object to print
     */
    public void println(Object x) {
        String line;
        Throwable t;
        StackTraceElement[] trace;
        int i;

        if (x instanceof Throwable) {
            t = (Throwable) x;
            trace = t.getStackTrace();
            line = t.toString() + "\n";
            for (i = 0; i < trace.length; i++)
                line += "\t" + trace[i].toString() + "\n";
            x = line;
        }

        printHeader();
        for (i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).println(x);
        flush();
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array starting at
     * offset <code>off</code> to this stream. If automatic flushing is enabled then
     * the <code>flush</code> method will be invoked.
     *
     * <p>
     * Note that the bytes will be written as given; to write characters that will
     * be translated according to the platform's default character encoding, use the
     * <code>print(char)</code> or <code>println(char)</code> methods.
     *
     * @param buf A byte array
     * @param off Offset from which to start taking bytes
     * @param len Number of bytes to write
     */
    public void write(byte buf[], int off, int len) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).write(buf, off, len);
        flush();
    }

    /**
     * Writes the specified byte to this stream. If the byte is a newline and
     * automatic flushing is enabled then the <code>flush</code> method will be
     * invoked.
     *
     * <p>
     * Note that the byte is written as given; to write a character that will be
     * translated according to the platform's default character encoding, use the
     * <code>print(char)</code> or <code>println(char)</code> methods.
     *
     * @param b The byte to be written
     * @see #print(char)
     * @see #println(char)
     */
    public void write(int b) {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).write(b);
        flush();
    }

    /**
     * Writes <code>b.length</code> bytes to this output stream.
     * <p>
     * The <code>write</code> method of <code>FilterOutputStream</code> calls its
     * <code>write</code> method of three arguments with the arguments
     * <code>b</code>, <code>0</code>, and <code>b.length</code>.
     * <p>
     * Note that this method does not call the one-argument <code>write</code>
     * method of its underlying output stream with the single argument
     * <code>b</code>.
     *
     * @param b the data to be written.
     * @exception IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b) throws IOException {
        printHeader();
        for (int i = 0; i < size(); i++)
            ((PrintStream) m_Streams.get(i)).write(b);
        flush();
    }

    /**
     * A convenience method to write a formatted string to this output stream using
     * the specified format string and arguments.
     *
     * <p>
     * An invocation of this method of the form {@code out.printf(format, args)}
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     * {@code
     *     out.format(format, args)
     * }
     * </pre>
     *
     * @param format A format string as described in
     *               <a href="../util/Formatter.html#syntax">Format string
     *               syntax</a>
     *
     * @param args   Arguments referenced by the format specifiers in the format
     *               string. If there are more arguments than format specifiers, the
     *               extra arguments are ignored. The number of arguments is
     *               variable and may be zero. The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               <cite>The Java&trade; Virtual Machine Specification</cite>. The
     *               behaviour on a {@code null} argument depends on the
     *               <a href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @throws                      java.util.IllegalFormatException If a format
     *                              string contains an illegal syntax, a format
     *                              specifier that is incompatible with the given
     *                              arguments, insufficient arguments given the
     *                              format string, or other illegal conditions. For
     *                              specification of all possible formatting errors,
     *                              see the <a href=
     *                              "../util/Formatter.html#detail">Details</a>
     *                              section of the formatter class specification.
     *
     * @throws NullPointerException If the {@code format} is {@code null}
     *
     * @return This output stream
     *
     * @since 1.5
     */
    @Override
    public PrintStream printf(String format, Object... args) {
        printHeader();
        for (int i = 0; i < size(); i++)
            m_Streams.get(i).printf(format, args);
        flush();
        return this;
    }

    /**
     * A convenience method to write a formatted string to this output stream using
     * the specified format string and arguments.
     *
     * <p>
     * An invocation of this method of the form {@code out.printf(l, format, args)}
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     * {@code
     *     out.format(l, format, args)
     * }
     * </pre>
     *
     * @param l      The {@linkplain Locale locale} to apply during formatting. If
     *               {@code l} is {@code null} then no localization is applied.
     *
     * @param format A format string as described in
     *               <a href="../util/Formatter.html#syntax">Format string
     *               syntax</a>
     *
     * @param args   Arguments referenced by the format specifiers in the format
     *               string. If there are more arguments than format specifiers, the
     *               extra arguments are ignored. The number of arguments is
     *               variable and may be zero. The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               <cite>The Java&trade; Virtual Machine Specification</cite>. The
     *               behaviour on a {@code null} argument depends on the
     *               <a href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @throws                      java.util.IllegalFormatException If a format
     *                              string contains an illegal syntax, a format
     *                              specifier that is incompatible with the given
     *                              arguments, insufficient arguments given the
     *                              format string, or other illegal conditions. For
     *                              specification of all possible formatting errors,
     *                              see the <a href=
     *                              "../util/Formatter.html#detail">Details</a>
     *                              section of the formatter class specification.
     *
     * @throws NullPointerException If the {@code format} is {@code null}
     *
     * @return This output stream
     *
     * @since 1.5
     */
    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        printHeader();
        for (int i = 0; i < size(); i++)
            m_Streams.get(i).printf(l, format, args);
        flush();
        return this;
    }

    /**
     * Appends the specified character to this output stream.
     *
     * <p>
     * An invocation of this method of the form {@code out.append(c)} behaves in
     * exactly the same way as the invocation
     *
     * <pre>
     * {@code
     *     out.print(c)
     * }
     * </pre>
     *
     * @param c The 16-bit character to append
     *
     * @return This output stream
     *
     * @since 1.5
     */
    @Override
    public PrintStream append(char c) {
        printHeader();
        for (int i = 0; i < size(); i++)
            m_Streams.get(i).append(c);
        flush();
        return this;
    }

    /**
     * Appends the specified character sequence to this output stream.
     *
     * <p>
     * An invocation of this method of the form {@code out.append(csq)} behaves in
     * exactly the same way as the invocation
     *
     * <pre>
     * {@code
     *     out.print(csq.toString())
     * }
     * </pre>
     *
     * <p>
     * Depending on the specification of {@code toString} for the character sequence
     * {@code csq}, the entire sequence may not be appended. For instance, invoking
     * then {@code toString} method of a character buffer will return a subsequence
     * whose content depends upon the buffer's position and limit.
     *
     * @param csq The character sequence to append. If {@code csq} is {@code null},
     *            then the four characters {@code "null"} are appended to this
     *            output stream.
     *
     * @return This output stream
     *
     * @since 1.5
     */
    @Override
    public PrintStream append(CharSequence csq) {
        printHeader();
        for (int i = 0; i < size(); i++)
            m_Streams.get(i).append(csq);
        flush();
        return this;
    }

    /**
     * Appends a subsequence of the specified character sequence to this output
     * stream.
     *
     * <p>
     * An invocation of this method of the form {@code out.append(csq, start, end)}
     * when {@code csq} is not {@code null}, behaves in exactly the same way as the
     * invocation
     *
     * <pre>
     * {@code
     *     out.print(csq.subSequence(start, end).toString())
     * }
     * </pre>
     *
     * @param csq   The character sequence from which a subsequence will be
     *              appended. If {@code csq} is {@code null}, then characters will
     *              be appended as if {@code csq} contained the four characters
     *              {@code "null"}.
     *
     * @param start The index of the first character in the subsequence
     *
     * @param end   The index of the character following the last character in the
     *              subsequence
     *
     * @return This output stream
     *
     * @throws IndexOutOfBoundsException If {@code start} or {@code end} are
     *                                   negative, {@code start} is greater than
     *                                   {@code end}, or {@code end} is greater than
     *                                   {@code csq.length()}
     *
     * @since 1.5
     */
    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        printHeader();
        for (int i = 0; i < size(); i++)
            m_Streams.get(i).append(csq, start, end);
        flush();
        return this;
    }

    /**
     * returns only the classname and the number of streams.
     *
     * @return only the classname and the number of streams
     */
    public String toString() {
        return this.getClass().getName() + ": " + m_Streams.size();
    }

    /**
     * Returns the revision string.
     *
     * @return the revision
     */
    public String getRevision() {
        return RevisionUtils.extract("$Revision$");
    }
}
