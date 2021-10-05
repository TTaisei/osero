#ifndef osero_h
#define osero_h

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

const bool BLACK = false;
const bool WHITE = true;

typedef unsigned long BOARD;

enum class TURN{
    black,
    white
};

enum class PLAY_WAY{
    human,
    random,
    sentinel
};

class osero{
    private:
        // board
        BOARD bw[2];
        int black_num = 0;
        int white_num = 0;
        char score[8][8];

        // other
        const int SIZE = 8;
        bool PLAYER;
        bool turn = BLACK;
        int bmethod, wmethod;
        void (osero:: * play_method[static_cast<int>(PLAY_WAY::sentinel)]) (int *, int *) = {
            &osero::que,
            &osero::random
        };

        // function
        bool check(int line, int col);
        bool check_all(void);
        void put(int line, int col);
        void printb(void);
        void count(void);
        void que(int * line, int * col);
        void random(int * line, int * col);
        int popcount(BOARD now);

    public:
        osero(int player_b, int player_w);
        ~osero();
        void play(void);
};

#endif