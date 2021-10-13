#ifndef osero_genetic_h
#define osero_genetic_h

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define INT(num) (static_cast<int>(num))
#define DOUBLE(num) (static_cast<double>(num))

const bool BLACK = false;
const bool WHITE = true;

typedef unsigned long BOARD;

enum class TURN{
    black,
    white
};

enum class PLAY_WAY{
    nhand,
    random,
    sentinel
};

class osero_genetic{
    private:
        // other
        const int SIZE = 8;
        bool turn = BLACK;
        int bmethod, wmethod;
        void (osero_genetic:: * play_method[INT(PLAY_WAY::sentinel)]) (int *, int *) = {
            &osero_genetic::nhand,
            &osero_genetic::random
        };

        // board
        BOARD bw[2];
        double eva[2][64];

        // function
        bool check(BOARD * now, int line, int col);
        bool check_all(void);
        bool count_last(void);
        void put(BOARD * now, int line, int col, bool turn);
        void printb(void);
        void nhand(int * line, int * col);
        void random(int * line, int * col);
        int popcount(BOARD now);
        double count(BOARD * now);
        double board_add(BOARD * now, int num, bool turn);

    public:
        osero_genetic(int player_b, int player_w);
        osero_genetic(int player_b, double * eva, int player_w);
        osero_genetic(int player_b, int player_w, double * eva);
        osero_genetic(int player_b, double * eva1, int player_w, double * eva2);
        ~osero_genetic();

        int read_goal[2] = {1, 1};
        int mode = 0;
        int player, computer;
        int srand_num = 0;
        bool print = false;

        bool play(void);
};

#endif