import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Solver {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("words_alpha.txt");
        Scanner fileScan = new Scanner(file);
        int numWords = 0;
        Trie trie = new Trie();
        while (fileScan.hasNextLine()) {
            String word = fileScan.nextLine();
            numWords++;
            trie.insert(word);
        }
        System.out.println("Number of words in dictionary: " + numWords);
        Scanner s = new Scanner(System.in);
        int row = 1;
        ArrayList<ArrayList<String>> board = new ArrayList<>();
        while (row <= 4) {
            System.out.println("Enter row #" + row + " of board separated by spaces:");
            boolean valid = false;
            while (!valid) {
                String[] letters = s.nextLine().split(" ");
                if (letters.length != 4) {
                    System.out.println("Invalid input; did not enter 4 characters. Try again:");
                } else {
                    valid = true;
                    board.add(new ArrayList<String>(Arrays.asList(letters)));
                }
            }    
            row++;    
        }
        System.out.println("Solving for the following board:");
        for (ArrayList<String> a : board) {
            for (String str : a) {
                System.out.print(str + " ");
            }
            System.out.println();
        }
        Game game = new Game(board, trie);
        System.out.println("--- SOLUTIONS ---");
        ArrayList<String> words = new ArrayList<>(game.solve());
        words.sort((s1, s2) -> s2.length() - s1.length());
        for (String word : words) {
            System.out.println(word);
        }
    }
}

public class Trie {
    private class Node {
        Node[] children;
        boolean isWord;

        public Node() {
            children = new Node[26];
            isWord = false;
        }
    }

    Node root;

    public Trie() {
        root = new Node();
    }

    public void insert(String word) {
        Node head = root;
        for (int i = 0; i < word.length(); i++) {
            char cur = word.charAt(i);
            int charIdx = cur - 'a';
            if (head.children[charIdx] == null)
                head.children[charIdx] = new Node();
            head = head.children[charIdx];
        }
        head.isWord = true;
    } 

    public boolean search(String word) {
        Node head = root;
        for (int i = 0; i < word.length(); i++) {
            char cur = word.charAt(i);
            int charIdx = cur - 'a';
            if (head.children[charIdx] == null)
                return false;
            head = head.children[charIdx];
        }
        return head.isWord;
    }   

    public boolean startsWith(String prefix) {
        Node head = root;
        for (int i = 0; i < prefix.length(); i++) {
            char cur = prefix.charAt(i);
            int charIdx = cur - 'a';
            if (head.children[charIdx] == null)
                return false;
            head = head.children[charIdx];
        }
        return true;
    }
}

public class Game {

    ArrayList<ArrayList<String>> board;
    boolean[][] visited;
    Trie trie;
    
    public Game(ArrayList<ArrayList<String>> b, Trie t) {
        board = b;
        trie = t;
        visited = new boolean[4][4];
    }

    // returns list of words and the associated max score
    public HashSet<String> solve() {
        HashSet<String> words = new HashSet<>();
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.get(i).size(); j++) {
                dfs(i, j, "", words);
            }
        }
        return words;
    }

    public void dfs(int i, int j, String build, HashSet<String> words) {
        if (i < 0 || i >= 4 || j < 0 || j >= 4 || visited[i][j]) {
            return;
        } else {
            char cur = board.get(i).get(j).charAt(0);
            build += cur;
            if (!trie.startsWith(build)) {
                return;
            }
            visited[i][j] = true;
            dfs(i + 1, j, build, words);
            dfs(i + 1, j + 1, build, words);
            dfs(i - 1, j, build, words);
            dfs(i - 1, j - 1, build, words);
            dfs(i, j + 1, build, words);
            dfs(i, j - 1, build, words);
            dfs(i - 1, j + 1, build, words);
            dfs(i + 1, j - 1, build, words);
            visited[i][j] = false;
            if (build.length() >= 3) {
                if (trie.search(build)) {
                    words.add(build);
                }
            }
        }
    }
}