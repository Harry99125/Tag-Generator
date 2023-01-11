import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Create html file(s) for counting words.
 *
 * @author Zening Teng Haojia feng
 *
 */
public final class tagCloud {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private tagCloud() {
    }

    private static final int MAX = 48;
    private static final int MIN = 11;

    /* alphabetical order */
    private static class SortWord
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Entry<String, Integer> o1,
                Entry<String, Integer> o2) {

            return o1.getKey().toLowerCase()
                    .compareTo(o2.getKey().toLowerCase());
        }
    }

    /* numeric order */
    private static class SortCount
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Entry<String, Integer> o1,
                Entry<String, Integer> o2) {

            return o2.getValue().compareTo(o1.getValue());
        }

    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */

    public static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        String word = "";
        boolean end = false;
        int i = position;

        if (separators.contains(text.charAt(position))) {
            while (i < text.length() && !end) {
                if (separators.contains(text.charAt(i))) {
                    word += text.charAt(i);
                } else {
                    end = true;
                }
                i++;
            }
        }
        if (!separators.contains(text.charAt(position))) {
            while (i < text.length() && !end) {
                if (!separators.contains(text.charAt(i))) {
                    word += text.charAt(i);
                } else {
                    end = true;
                }
                i++;
            }
        }
        return word;
    }

    /**
     * Output the header for the index page. The index page will include title,
     * and tagcloud.
     *
     * @param out
     *            the output stream
     * @param num
     *            the top # number
     * @param words
     *            HashMap with words and counting
     * @param input
     *            name of the input file
     * @param biggest
     *            biggest count in the map
     * @param smallest
     *            smallest count in the map
     * @updates out.content
     * @requires out.is_open
     * @ensures out.content = #out.content * [the HTML "opening" tags]
     */
    public static void outputHeader(PrintWriter out, String input, int num,
            List<Map.Entry<String, Integer>> words, int biggest, int smallest)
            throws IOException {

        assert out != null : "Violation of: out is not null";

        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + "Top" + num + "words in " + input + "</title>");
        out.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/web-sw2/assignments/projects/tag-cloud-generator/data/tagcloud.css\" rel=\"stylesheet\"type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>" + "Top " + num + " words in " + input + "</h2>");
        out.println("<hr>");
        out.println("<div class=\"cdiv\">");
        out.println("<p class=\"cbox\">");
        for (Map.Entry<String, Integer> temp : words) {
            /* add every words and counts in to tag cloud */

            out.print("<span style=\"cursor:default\" class=\"f");
            int plus = biggest / (MAX - MIN);
            int start = smallest;
            int size = MIN;

            while (start < temp.getValue()) {
                start += plus;
                size++;
            }
            if (size > MAX) {
                size = MAX;
            }
            out.print(size);
            out.print("\" title=\"count: ");
            out.println(temp.getValue() + "\">" + temp.getKey() + "</span>");

        }
        out.println("</p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");

    }

    /*
     * Override compare method from Comparator to compare two string based on
     * alphabetize
     *
     *
     */

    /**
     * Read each line in the file to put every single words and their number
     * into queue and map. also sort the queue with alphabetize sequence.
     *
     * @param in
     *            File for reading
     *
     * @param mapForWords
     *            Map that stores words and how many times each of them appear
     *            in the file.
     * @param separators
     *            used for separate words in the file for counting.
     *
     * @requires in is open,
     * @ensures terms=alphabetize, combine = {terms, definitions}
     */
    public static void processing(BufferedReader in, Set<Character> separators,
            Map<String, Integer> mapForWords

    ) throws IOException {
        String line = null;

        line = in.readLine();

        while (line != null) {

            int i = 0;

            String content = "";
            String contentLow = "";
            while (i < line.length()) {
                boolean seperate = false;
                content = nextWordOrSeparator(line, i, separators);
                /*
                 * check the character at the 0 pos for every words, it they are
                 * not seperator, then add them into and map
                 */
                char firstLetter = content.charAt(0);
                if (separators.contains(firstLetter)) {
                    seperate = true;
                } else {
                    seperate = false;
                    contentLow = content.toLowerCase();
                }
                if (!seperate) {
                    if (!mapForWords.containsKey(contentLow)) {
                        mapForWords.put(contentLow, 1);

                    } else {
                        /*
                         * if the word is already in the map, then add 1, which
                         * means 1 more same word counted
                         */

                        mapForWords.replace(contentLow,
                                mapForWords.get(contentLow),
                                mapForWords.get(contentLow) + 1);
                    }
                }
                i += content.length();

            }

            line = in.readLine();

        }
    }

    /**
     * add all separators to a set.
     *
     * @param separator
     *            an empty set for separators
     *
     * @ensures separator={String of separators}
     */
    public static void addSep(Set<Character> separator

    ) {
        separator.add(',');
        separator.add('\t');
        separator.add('\n');
        separator.add('\r');
        separator.add(' ');
        separator.add('.');
        separator.add('-');
        separator.add('[');
        separator.add(']');
        separator.add('(');
        separator.add(')');
        separator.add('{');
        separator.add('}');
        separator.add('/');
        separator.add(';');
        separator.add(':');
        separator.add('?');
        separator.add('"');
        separator.add('!');
        separator.add('\'');

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     * @throws Exception
     */
    public static void main(String[] args) {
        BufferedReader input;
        PrintWriter output;
        BufferedReader enter = new BufferedReader(
                new InputStreamReader(System.in));
        Comparator<Map.Entry<String, Integer>> count = new SortCount();
        Comparator<Map.Entry<String, Integer>> alpha = new SortWord();
        Map<String, Integer> mapForWords = new HashMap<>();

        List<Map.Entry<String, Integer>> tempMap;
        tempMap = new LinkedList<Map.Entry<String, Integer>>();

        int top = 0;

        Set<Character> separator = new HashSet<>();
        addSep(separator);
        System.out.println("Please enter location of input file:");
        String name = null;
        try {

            name = enter.readLine();
        } catch (IOException e) {

            System.err.println("Failing reading file");
            return;
        }
        try {
            input = new BufferedReader(new FileReader(name));

        } catch (IOException e) {
            System.err.println("Error input file");
            return;
        }
        try {
            processing(input, separator, mapForWords);
        } catch (IOException e) {
            System.err.println("Error input file");
            return;
        }
        System.out.println("how many words you want?:");

        try {
            top = Integer.parseInt(enter.readLine());
            while (top > mapForWords.size() || top <= 0) {
                System.out.println(
                        "the input is invalid, please enter a new number");
                top = Integer.parseInt(enter.readLine());
            }
        } catch (NumberFormatException | IOException e) {
            System.err.println("Please enter an number");
            return;
        }

        List<Map.Entry<String, Integer>> mapCount;
        mapCount = new LinkedList<Map.Entry<String, Integer>>();
        mapCount.addAll(mapForWords.entrySet());
        Collections.sort(mapCount, count);

        int i = 0;
        for (i = 0; i < top; i++) {
            Map.Entry<String, Integer> p = mapCount.get(i);
            tempMap.add(p);
        }

        System.out.println("Please enter output location:");
        String location = null;
        try {
            location = enter.readLine();
        } catch (IOException e) {
            System.err.println("Error output filename");
            return;
        }
        try {
            output = new PrintWriter(
                    new BufferedWriter(new FileWriter(location)));
        } catch (IOException e) {
            System.err.println("Error output file");
            return;
        }
        /* get the biggest count and smallest count in the map */
        int small = tempMap.get(tempMap.size() - 1).getValue();
        int big = tempMap.get(0).getValue();

        Collections.sort(tempMap, alpha);
        try {
            outputHeader(output, name, top, tempMap, big, small);
        } catch (IOException e) {
            System.err.println("Error output file");
            return;
        }

        try {
            input.close();
        } catch (IOException e) {
            System.err.println("Error close file");
            return;
        }
        output.close();

        try {
            enter.close();
        } catch (IOException e) {
            System.err.println("Error close file");
            return;
        }

    }

}
