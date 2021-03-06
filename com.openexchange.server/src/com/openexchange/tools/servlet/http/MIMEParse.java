/*
 * Copyright (c) 2010 Joe Gregorio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.openexchange.tools.servlet.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * MIME-Type Parser
 * <p>
 * This class provides basic functions for handling mime-types. It can handle matching mime-types against a list of media-ranges.
 * See <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1">section 14.1 of the HTTP specification [RFC 2616]</a>
 * for a complete explanation.
 * <p>
 * A port to Java of Joe Gregorio's MIME-Type Parser:<br>
 * <br>
 * http://code.google.com/p/mimeparse/<br>
 * <br>
 * Ported by Tom Zellman <tzellman@gmail.com>.
 *
 */
public final class MIMEParse {

    // hidden
    private MIMEParse() {
        super();
    }

    /**
     * Parse results container
     */
    protected static class ParseResults {

        String type;

        String subType;

        // !a dictionary of all the parameters for the media range
        Map<String, String> params;

        @Override
        public String toString() {
            StringBuffer s = new StringBuffer("('" + type + "', '" + subType + "', {");
            for (Entry<String, String> e : params.entrySet()) {
                s.append("'" + e.getKey() + "':'" + e.getValue() + "',");
            }
            return s.append("})").toString();
        }
    }

    /**
     * Carves up a mime-type and returns a ParseResults object
     * <p>
     * For example, the media range 'application/xhtml;q=0.5' would get parsed
     * into:<br>
     * <code>('application', 'xhtml', {'q', '0.5'})</code>
     */
    protected static ParseResults parseMimeType(String mimeType) {
        String[] parts = StringUtils.split(mimeType, ";");
        ParseResults results = new ParseResults();
        results.params = new HashMap<String, String>(parts.length);

        for (int i = 1; i < parts.length; ++i) {
            String p = parts[i];
            String[] subParts = StringUtils.split(p, '=');
            if (subParts.length == 2) {
                results.params.put(subParts[0].trim(), subParts[1].trim());
            }
        }
        String fullType = parts[0].trim();

        // Java URLConnection class sends an Accept header that includes a single "*" - Turn it into a legal wildcard.
        if (fullType.equals("*")) {
            fullType = "*/*";
        }
        String[] types = StringUtils.split(fullType, "/");
        results.type = types[0].trim();
        results.subType = types[1].trim();
        return results;
    }

    /**
     * Carves up a media range and returns a ParseResults.
     * <p>
     * For example, the media range 'application/*;q=0.5' would get parsed into:<br>
     * <code>('application', '*', {'q', '0.5'})</code>
     * <p>
     * In addition this function also guarantees that there is a value for 'q'
     * in the params dictionary, filling it in with a proper default if
     * necessary.
     *
     * @param range
     */
    protected static ParseResults parseMediaRange(String range) {
        ParseResults results = parseMimeType(range);
        String q = results.params.get("q");
        float f = NumberUtils.toFloat(q, 1);
        if (StringUtils.isBlank(q) || f < 0 || f > 1) {
            results.params.put("q", "1");
        }
        return results;
    }

    /**
     * Splits specified comma-separated media ranges and returns appropriate ParseResults.
     *
     * @param ranges The media ranges to parse
     * @return The parse results
     */
    protected static List<ParseResults> parseRanges(String ranges) {
        String[] arrRanges = StringUtils.split(ranges, ',');
        List<ParseResults> results = new ArrayList<ParseResults>(arrRanges.length);
        for (String r : arrRanges) {
            results.add(parseMediaRange(r));
        }
        return results;
    }

    /**
     * Structure for holding a fitness/quality combo
     */
    protected static class FitnessAndQuality implements Comparable<FitnessAndQuality> {

        int fitness;
        float quality;
        String mimeType;// optionally used

        public FitnessAndQuality(int fitness, float quality) {
            this.fitness = fitness;
            this.quality = quality;
        }

        @Override
        public int compareTo(FitnessAndQuality o) {
            if (fitness != o.fitness) {
                return fitness < o.fitness ? -1 : 1;
            }

            return Float.compare(quality, o.quality);
        }
    }

    /**
     * Find the best match for a given mimeType against a list of media_ranges
     * that have already been parsed by MimeParse.parseMediaRange(). Returns a
     * tuple of the fitness value and the value of the 'q' quality parameter of
     * the best match, or (-1, 0) if no match was found. Just as for
     * quality_parsed(), 'parsed_ranges' must be a list of parsed media ranges.
     *
     * @param mimeType
     * @param parsedRanges
     */
    protected static FitnessAndQuality fitnessAndQualityParsed(String mimeType, Collection<ParseResults> parsedRanges) {
        int bestFitness = -1;
        float bestFitQ = 0;
        ParseResults target = parseMediaRange(mimeType);

        for (ParseResults range : parsedRanges) {
            if ((target.type.equals(range.type) || range.type.equals("*") || target.type.equals("*")) && (target.subType.equals(range.subType) || range.subType.equals("*") || target.subType.equals("*"))) {
                for (String k : target.params.keySet()) {
                    int paramMatches = 0;
                    if (!k.equals("q") && range.params.containsKey(k) && target.params.get(k).equals(range.params.get(k))) {
                        paramMatches++;
                    }
                    int fitness = (range.type.equals(target.type)) ? 100 : 0;
                    fitness += (range.subType.equals(target.subType)) ? 10 : 0;
                    fitness += paramMatches;
                    if (fitness > bestFitness) {
                        bestFitness = fitness;
                        bestFitQ = NumberUtils.toFloat(range.params.get("q"), 0);
                    }
                }
            }
        }
        return new FitnessAndQuality(bestFitness, bestFitQ);
    }

    /**
     * Find the best match for a given mime-type against a list of ranges that
     * have already been parsed by parseMediaRange(). Returns the 'q' quality
     * parameter of the best match, 0 if no match was found. This function
     * bahaves the same as quality() except that 'parsed_ranges' must be a list
     * of parsed media ranges.
     *
     * @param mimeType
     * @param parsedRanges
     * @return
     */
    protected static float qualityParsed(String mimeType, Collection<ParseResults> parsedRanges) {
        return fitnessAndQualityParsed(mimeType, parsedRanges).quality;
    }

    /**
     * Returns the quality <code>'q'</code> of a mime-type when compared against the media ranges.
     *
     * @param mimeType The MIME type to determine the quality for
     * @param ranges The ranges
     */
    public static float quality(String mimeType, String ranges) {
        List<ParseResults> results = parseRanges(ranges);
        return qualityParsed(mimeType, results);
    }

    /**
     * Returns the qualities <code>'q'</code> of given mime-types when compared against the media ranges.
     *
     * @param mimeTypes The MIME types to determine the quality for
     * @param ranges The ranges
     */
    public static float[] qualities(List<String> mimeTypes, String ranges) {
        if (null == mimeTypes) {
            return new float[0];
        }

        int size = mimeTypes.size();
        if (size <= 0) {
            return new float[0];
        }

        List<ParseResults> results = parseRanges(ranges);

        float[] qualities = new float[size];
        for (int i = size; i-- > 0;) {
            String mimeType = mimeTypes.get(i);
            qualities[i] = qualityParsed(mimeType, results);
        }
        return qualities;
    }

    /**
     * Takes a list of supported mime-types and finds the best match for all the
     * media-ranges listed in header. The value of header must be a string that
     * conforms to the format of the HTTP Accept: header. The value of
     * 'supported' is a list of mime-types.
     *
     * MimeParse.bestMatch(Arrays.asList(new String[]{"application/xbel+xml",
     * "text/xml"}), "text/*;q=0.5,*; q=0.1") 'text/xml'
     *
     * @param supported
     * @param header
     * @return
     */
    public static String bestMatch(Collection<String> supported, String header) {
        List<ParseResults> parseResults = parseRanges(header);

        LinkedList<FitnessAndQuality> weightedMatches = new LinkedList<FitnessAndQuality>();
        for (String s : supported) {
            FitnessAndQuality fitnessAndQuality = fitnessAndQualityParsed(s, parseResults);
            fitnessAndQuality.mimeType = s;
            weightedMatches.add(fitnessAndQuality);
        }
        Collections.sort(weightedMatches);

        FitnessAndQuality lastOne = weightedMatches.getLast();
        return NumberUtils.compare(lastOne.quality, 0) != 0 ? lastOne.mimeType : "";
    }


}
