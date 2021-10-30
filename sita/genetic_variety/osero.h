#ifndef osero_h
#define osero_h

#include <stdio.h>
#include <stdlib.h>

#define INT(num) (static_cast<int>(num))
#define DOUBLE(num) (static_cast<double>(num))

typedef unsigned long BOARD;

enum class TURN{
    black,
    white
};

enum class PLAY_WAY{
    nhand,
    nhand_custom,
    nleast,
    nmost,
    random,
    human,
    sentinel
};

class osero{
    protected:
        // other
        const int SIZE = 8;
        bool turn = false;
        int bmethod, wmethod;
        void (osero:: * play_method[INT(PLAY_WAY::sentinel)]) (int *, int *) = {
            &osero::nhand,
            &osero::nhand_custom,
            &osero::nleast,
            &osero::nmost,
            &osero::random,
            &osero::human
        };

        // board
        BOARD bw[2];
        double eva[2][64];

        // function
        bool check(BOARD * now, int line, int col, bool turn);
        bool check_all(void);
        int check_place(BOARD * now, int num, bool turn, bool tar_turn);
        void put(BOARD * now, int line, int col, bool turn);
        void printb(void);
        void nhand(int * line, int * col);
        void nhand_custom(int * line, int * col);
        void nleast(int * line, int * col);
        void nmost(int * line, int * col);
        void random(int * line, int * col);
        void human(int * line, int * col);
        int popcount(BOARD now);
        double count(BOARD * now);
        double board_add(BOARD * now, int num, bool turn, bool iscustom);
        void count_last(void);
        void reset(void);

    public:
        osero();
        osero(int player_b, int player_w);
        osero(int player_b, double * eva, int player_w);
        osero(int player_b, int player_w, double * eva);
        osero(int player_b, double * eva1, int player_w, double * eva2);
        virtual ~osero();

        int player, computer;
        int srand_num = 99;
        int read_goal[2] = {1, 1};

        void play(void);
};

#endif