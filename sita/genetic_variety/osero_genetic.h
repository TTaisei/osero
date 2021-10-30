#ifndef osero_genetic_h
#define osero_genetic_h

#include "osero.h"

class osero_genetic : public osero{
    private:
        int count_last(void);

    public:
        osero_genetic(int player_b, int player_w):
            osero(player_b, player_w){};
        osero_genetic(int player_b, double * eva, int player_w):
            osero(player_b, eva, player_w){};
        osero_genetic(int player_b, int player_w, double * eva):
            osero(player_b, player_w, eva){};
        osero_genetic(int player_b, double * eva1, int player_w, double * eva2):
            osero(player_b, eva1, player_w, eva2){};
        ~osero_genetic();

        virtual int play(void);
};

#endif