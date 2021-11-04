#include <string>
#include "osero_learn.h"

const int PLAY_NUM = 10000;

int main(void){
    int i, j;
    osero_learn * run = new osero_learn();
    FILE * datap;
    std::string data;

    datap = fopen("data.csv", "w");
    data = "turn_num,turn,num,put_place,my_score,opp_score,";
    data += "my_top_score,my_sec_score,my_thr_score,";
    data += "opp_top_score,opp_sec_score,opp_thr_score,";
    data += "last_black_score,last_white_score";
    fprintf(datap, "%s\n", data.c_str());

    for (i = 0; i < PLAY_NUM; i++){
        run -> srand_num = i;
        run -> reset();
        run -> play();
        for (j = 0; j < run -> turn_num; j++){
            fprintf(
                datap,
                "%d,%d,%d,%d,%lf,%lf,%lf,%lf,%lf,%lf,%lf,%lf,%d,%d\n",
                j,
                INT(run -> history[j]["turn"]),
                INT(run -> history[j]["num"]),
                INT(run -> history[j]["put_place"]),
                run -> history[j]["my_score"],
                run -> history[j]["opp_score"],
                run -> history[j]["my_top_score"],
                run -> history[j]["my_sec_score"],
                run -> history[j]["my_thr_score"],
                run -> history[j]["opp_top_score"],
                run -> history[j]["opp_sec_score"],
                run -> history[j]["opp_thr_score"],
                run -> bw_score[0],
                run -> bw_score[1]
            );
        }
    }

    delete run;
    fclose(datap);

    return 0;
}