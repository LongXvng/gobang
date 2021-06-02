import re

# 棋盘宽高
WIDTH, HEIGHT = 16, 16
# 棋子状态
BLANK = '_'
BLACK = '●'
WHITE = '○'
# 游戏状态
GAME_TYPE_CLOSE = 0
GAME_TYPE_REGRET = 1
GAME_TYPE_JUMP = 2

# 判断连珠增量 [横向    纵向  右下到左上 左下到右上]
direction = [[0, 1], [1, 0], [1, 1], [1, -1]]
# 棋盘 [16][16]
chessboard = []
# 最后一步落子位置 [x, y, user]
lastJump = []


def init():
    global chessboard
    chessboard = [[hex(y)[2] if x == 0 else hex(x)[2] if y == 0 else BLANK for x in range(0, WIDTH)] for y in
                  range(0, HEIGHT)]


def paint():
    print(*("  ".join(chessboard[i]) for i in range(WIDTH)), sep='\n')


def getInput(_user):
    while True:
        print("\n* 落子格式:x(num), y(num) 悔棋:regret 退出:quit")
        inputStr = input("{}玩家 请落子>".format("A(%s)" % BLACK if _user == 0 else "B(%s)" % WHITE))

        if "quit" == inputStr.lower() or "exit" == inputStr.lower():
            return [GAME_TYPE_CLOSE, 0, 0]

        if "regret" == inputStr.lower():
            chessboard[lastJump[0]][lastJump[1]] = BLANK
            return [GAME_TYPE_REGRET, 0, 0]

        if not re.match(r'[0-9a-fA-F][\s]*[,，][\s]*[0-9a-fA-F]', inputStr):
            print("输入格式错误！请重新落子")
            continue

        locations = inputStr.split(",")
        if len(locations) == 2:
            x = int(locations[0], 16)
            y = int(locations[1], 16)
        else:
            locations = inputStr.split("，")
            x = int(locations[0], 16)
            y = int(locations[1], 16)
        return [GAME_TYPE_JUMP, x, y]


def jump(_x, _y, _user):
    global lastJump
    if chessboard[_x][_y] != BLANK:
        print("该位置已有棋子！请重新落子")
        return False

    chessboard[_x][_y] = BLACK if _user == 0 else WHITE
    lastJump = [_x, _y, chessboard[_x][_y]]
    return True


def calWin():
    global lastJump
    if not len(lastJump):
        return [False, 0]

    last_x, last_y, chessman = lastJump
    # 横向 纵向 右下到左上 左下到右上
    for offset_x, offset_y in direction:
        _isWin, _winUser = calDirection(last_x, last_y, offset_x, offset_y, chessman)
        if _isWin:
            return [_isWin, _winUser]

    return [False, 0]


def calDirection(_x, _y, offset_x, offset_y, _chessman):
    count = 0
    while True:
        if 1 < _x < WIDTH and 1 < _y < HEIGHT:
            _x += offset_x
            _y += offset_y
            if chessboard[_x][_y] != _chessman:
                break
        else:
            break
    while True:
        if 1 < _x < WIDTH and 1 < _y < HEIGHT:
            _x += -1 * offset_x
            _y += -1 * offset_y
            if chessboard[_x][_y] == _chessman:
                count += 1
                if count >= 5:
                    break
            else:
                break
        else:
            break
    return [True if count == 5 else False, 0 if _chessman == BLACK else WHITE]


i = 0
init()
while True:
    paint()
    isWin, winUser = calWin()

    if not isWin:
        user = i % 2

        gameType, x, y = getInput(user)

        if gameType == GAME_TYPE_CLOSE:
            break
        elif gameType == GAME_TYPE_REGRET:
            i -= 1
            continue

        if jump(x, y, user):
            i += 1
    else:
        print("游戏结束！ {}玩家获胜 双方落子{}手".format("A(%s)" % BLACK if winUser == 0 else "B(%s)" % WHITE, i))
        break
