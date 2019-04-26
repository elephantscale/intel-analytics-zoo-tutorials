package com.elephantscale.apilab1;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.intel.analytics.zoo.pipeline.inference.JTensor;

import java.util.ArrayList;
import java.util.List;


@RestController
public class HelloController {
    
    @RequestMapping("/")
    public String index() {
        return "Recommendations Service\n";
    }

    @RequestMapping("/recs")
    public String getRecs() {
        String modelPath = System.getProperty("MODEL_PATH", "model.bin");

        NueralCFJModel rcm = new NueralCFJModel();

        rcm.load(modelPath);

        List<UserItemPair> userItemPairs = new ArrayList<>();
        for(int i= 1; i < 10; i++){
           userItemPairs.add(new UserItemPair(i, i+1));
        }

        List<List<JTensor>> jts = rcm.preProcess(userItemPairs);

        List<List<JTensor>> finalResult = rcm.predict(jts);

        String returnVal = "";

        for(List<JTensor> fjt : finalResult){
            for(JTensor t: fjt){
                returnVal += t + "\n";
            }
        }
        return returnVal;
    }
}
