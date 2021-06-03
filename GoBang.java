package com.test.openinstall;

import java.util.Scanner;

public class GoBang {
    // 棋盘宽高
    public static final int WIDTH = 16, HEIGHT = 16;
    // 棋子状态
    public static final String BLANK = "_";
    public static final String BLACK = "●";
    public static final String WHITE = "○";
    // 游戏状态
    public static final int GAME_TYPE_CLOSE = 0;
    public static final int GAME_TYPE_REGRET = 1;
    public static final int GAME_TYPE_JUMP = 2;

    // 判断连珠增量 [横向 纵向 右下到左上 左下到右上]
    public static int[][] direction;
    // 棋盘
    public static String[][] chessboard;
    // 最后一步落子位置 [x, y, chessman]
    public static Object[] lastJump;
    // 接收用户输入
    public static Scanner scanner;


    static {
        direction = new int[][]{{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        chessboard = new String[WIDTH][HEIGHT];
        scanner = new Scanner(System.in);
    }

    public static void init() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (x == 0) {
                    chessboard[x][y] = Integer.toHexString(x);
                } else {
                    if (y == 0) {
                        chessboard[x][y] = Integer.toHexString(y);
                    } else {
                        chessboard[x][y] = BLANK;
                    }
                }
            }
        }
    }

    public static void paint() {
        for (String[] line : chessboard) {
            for (String x : line) {
                System.out.print(x + "  ");
            }
            System.out.println();
        }
    }

    public static int[] getInput(int user) {
        while (true) {
            System.out.println("\n* 落子格式:x(num), y(num) 悔棋:regret 退出:quit");
            System.out.println(String.format("%s(%s)玩家 请落子>", user == 0 ? "A" : "B", user == 0 ? BLACK : WHITE));
            String inputStr = scanner.next();

            if ("quit".equals(inputStr.toLowerCase()) || "exit".equals(inputStr.toLowerCase())) {
                return new int[]{GAME_TYPE_CLOSE, 0, 0};
            }

            if ("regret".equals(inputStr.toLowerCase())) {
                chessboard[(int) lastJump[0]] [(int) lastJump[1]] = BLANK;
                return new int[]{GAME_TYPE_REGRET, 0, 0};
            }

            if (!inputStr.matches("[0-9a-fA-F][\\s]*[,，][\\s]*[0-9a-fA-F]")) {
                System.out.println("输入格式错误！请重新落子");
                continue;
            }

            String[] locations = inputStr.split(",");
            int x, y;
            if (locations.length == 2) {
                x = Integer.parseInt(locations[0]);
                y = Integer.parseInt(locations[1]);
            } else {
                locations = inputStr.split("，");
                x = Integer.parseInt(locations[0]);
                y = Integer.parseInt(locations[1]);
            }
            return new int[]{GAME_TYPE_JUMP, x, y};
        }
    }


    public static boolean jump(int _x, int _y, int _user) {
        if (!BLANK.equals(chessboard[_x][_y])) {
            System.out.println("该位置已有棋子！请重新落子");
            return false;
        }

        chessboard[_x][_y] = _user == 0 ? BLACK : WHITE;
        lastJump = new Object[]{_x, _y, chessboard[_x][_y]};
        return true;
    }

    public static Object[] checkWin() {
        if (lastJump == null || lastJump.length == 0) {
            return new Object[]{false, 0};
        }

        int last_x = (int) lastJump[0];
        int last_y = (int) lastJump[1];
        String chessman = (String) lastJump[2];

        // 横向 纵向 右下到左上 左下到右上
        for (int[] offset : direction) {
            Object[] result = calDirection(last_x, last_y, offset[0], offset[1], chessman);
            boolean _isWin = (boolean) result[0];
            if (_isWin) {
                return result;
            }
        }
        return new Object[]{false, 0};
    }

    public static Object[] calDirection(int _x, int _y, int offset_x, int offset_y, String _chessman) {
        int count = 0;
        while (true) {
            if (_x > 1 && _x < WIDTH && _y > 1 && _y < HEIGHT) {
                _x += offset_x;
                _y += offset_y;
                if (!chessboard[_x][_y].equals(_chessman)) {
                    break;
                }
            } else {
                break;
            }
        }
        while (true) {
            if (_x > 1 && _x < WIDTH && _y > 1 && _y < HEIGHT) {
                _x += -1 * offset_x;
                _y += -1 * offset_y;
                if (chessboard[_x][_y].equals(_chessman)) {
                    count += 1;
                    if (count >= 5) {
                        break;
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return new Object[]{count == 5, BLACK.equals(_chessman) ? 0 : 1};
    }

    public static void main(String[] args) {
        int i = 0;

        init();
        while (true) {
            paint();
            Object[] result = checkWin();
            boolean isWin = (boolean) result[0];
            int winUser = (int) result[1];

            if (!isWin) {
                int user = i % 2;

                int[] inputs = getInput(user);
                int gameType = inputs[0];
                int x = inputs[1];
                int y = inputs[2];

                if (gameType == GAME_TYPE_CLOSE) {
                    break;
                } else if (gameType == GAME_TYPE_REGRET) {
                    i -= 1;
                    continue;
                }

                if (jump(x, y, user)) {
                    i += 1;
                }
            } else {
                System.out.println(String.format("游戏结束！ %s(%s)玩家获胜 双方落子%d手", winUser == 0 ? "A" : "B", winUser == 0 ? BLACK : WHITE, i));
                break;
            }
        }
    }

}
