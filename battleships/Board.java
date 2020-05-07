public class Board {
    public int[][] board;
    public boolean over;
    public String[] positions = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };

    // initializing the board
    Board() {
        board = new int[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = 0;
            }
        }
    }

    boolean add_ship(String head, int orientation, int type) {
        String[] head_pos = head.split("");
        int row = get_row(head_pos[0].toUpperCase());
        int col = Integer.parseInt(head_pos[1]);

        // checks if theres a ship on the way
        if (test_ship_position(row, col, type, orientation)) {
            int ship_size = 6 - type;
            if (orientation == 0) { // horizontal
                for (int i = 0; i < ship_size; i++) {
                    board[row][col + i] = type;
                }

                return true;
            } else if (orientation == 1) { // vertical
                for (int i = 0; i < ship_size; i++) {
                    board[row + i][col] = type;
                }

                return true;
            } else
                return false;
        } else
            return false;

    }

    boolean test_ship_position(int head_row, int head_col, int type, int orientation) {
        int ship_size = 6 - type;
        if (orientation == 0) { // HORIZONTAL
            if (head_col + ship_size > 10) {
                return false;
            } else {
                for (int i = head_col; i < ship_size; i++) {
                    if (board[head_row][i] != 0) {
                        return false;
                    }
                }
                return true;
            }
        } else if (orientation == 1) { // VERTICAL
            if (head_row + ship_size > 10) {
                return false;
            } else {
                for (int i = head_row; i < ship_size; i++) {
                    if (board[i][head_col] != 0) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    boolean check_hit(String shot) {
        String[] shot_pos = shot.split("");
        int row = get_row(shot_pos[0].toUpperCase());
        int col = Integer.parseInt(shot_pos[1]);

        if (board[row][col] != 0 && board[row][col] != -1) {
            board[row][col] = -1;
            return true;
        }
        return false;
    }

    // returns number for row
    int get_row(String pos) {
        int i = 0;
        while (!positions[i].equals(pos)) {
            i++;
        }

        return i;
    }

    void print_board() {
        System.out.print("  ");
        for (int i = 0; i < 10; i++) {
            System.out.print(i + " ");
        }
        System.out.print("\n");

        for (int i = 0; i < 10; i++) {
            System.out.print(positions[i] + " ");
            for (int j = 0; j < 10; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.print("\n");
        }
    }

    boolean check_gameover() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board[i][j] != -1 && board[i][j] != 0)
                    return false;
            }
        }
        return true;
    }

    String get_pos(int row, int col) {
        return positions[row] + col;
    }
}