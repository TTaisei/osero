#ifndef osero_learn_h
#define osero_learn_h

#include <unordered_map>
#include <string>
#include "osero.h"

class osero_learn : public osero{
    private:
        const double t_eva[64] = {
             1.0, -0.6,  0.6,  0.4,  0.4,  0.6, -0.6,  1.0,
            -0.6, -0.8,  0.0,  0.0,  0.0,  0.0, -0.8, -0.6,
             0.6,  0.0,  0.8,  0.6,  0.6,  0.8,  0.0,  0.6,
             0.4,  0.0,  0.6,  0.0,  0.0,  0.6,  0.0,  0.4,
             0.4,  0.0,  0.6,  0.0,  0.0,  0.6,  0.0,  0.4,
             0.6,  0.0,  0.8,  0.6,  0.6,  0.8,  0.0,  0.6,
            -0.6, -0.8,  0.0,  0.0,  0.0,  0.0, -0.8, -0.6,
             1.0, -0.6,  0.6,  0.4,  0.4,  0.6, -0.6,  1.0
        };

        void nhand_evacustom(int * line, int * col);
        void count_last(void);
        double cal_evascore(BOARD * now, int turn);

    public:
        osero_learn();
        ~osero_learn();

        void play(void);

        int bw_score[2];
        int turn_num;
        std::unordered_map<std::string, double> history[60];
};

#endif