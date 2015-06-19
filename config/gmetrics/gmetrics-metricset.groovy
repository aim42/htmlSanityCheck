// GMetrics Configuration
//
metricset {

    description 'htmlSanityChecks extensive GMetrics MetricSet'

    // ABCMetric
   ABC {
        functions = ['average', 'maximum']
    }


    // final COBERTURA_FILE = '.coverage.xml'

    //def coberturaMetric = CoberturaLineCoverage {
    //    coberturaFile = COBERTURA_FILE
    //    functions = ['total']
    //}

    //CRAP {
    //    functions = ['total']
    //    coverageMetric = coberturaMetric
    //}


    CyclomaticComplexity { functions = ['average'] }

    EfferentCoupling { functions = ['total'] }
    AfferentCoupling { functions = ['total'] }


    ClassCount { functions = ['total'] }

    MethodCount { functions = ['total'] }

    MethodLineCount { functions = ['total', "average"]
    }


}
