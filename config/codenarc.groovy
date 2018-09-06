ruleset {

        description '''
        A Groovy RuleSet containing many of the CodeNarc standard Rules, 
        grouped by category.
        '''

        // rulesets/basic.xml
        AssertWithinFinallyBlock
        AssignmentInConditional
        BigDecimalInstantiation
        BitwiseOperatorInConditional
        BooleanGetBoolean
        BrokenNullCheck
        BrokenOddnessCheck
        ClassForName
        ComparisonOfTwoConstants
        ComparisonWithSelf
        ConstantAssertExpression
        ConstantIfExpression
        ConstantTernaryExpression
        DeadCode
        DoubleNegative
        DuplicateCaseStatement
        DuplicateMapKey
        DuplicateSetValue
        EmptyCatchBlock
        EmptyClass
        EmptyElseBlock
        EmptyFinallyBlock
        EmptyForStatement
        EmptyIfStatement
        EmptyInstanceInitializer
        EmptyMethod
        EmptyStaticInitializer
        EmptySwitchStatement
        EmptySynchronizedStatement
        EmptyTryBlock
        EmptyWhileStatement
        EqualsAndHashCode
        EqualsOverloaded
        ExplicitGarbageCollection
        ForLoopShouldBeWhileLoop
        HardCodedWindowsFileSeparator
        HardCodedWindowsRootDirectory
        IntegerGetInteger
        MultipleUnaryOperators
        RandomDoubleCoercedToZero
        RemoveAllOnSelf
        ReturnFromFinallyBlock
        ThrowExceptionFromFinallyBlock

        // rulesets/braces.xml
        ElseBlockBraces
        ForStatementBraces
        IfStatementBraces
        WhileStatementBraces



        // rulesets/convention.xml
        ConfusingTernary
        CouldBeElvis
        CouldBeSwitchStatement
        FieldTypeRequired
        HashtableIsObsolete
        IfStatementCouldBeTernary
        InvertedCondition
        InvertedIfElse
        LongLiteralWithLowerCaseL
        MethodParameterTypeRequired
        MethodReturnTypeRequired
        NoDef
        NoJavaUtilDate 
        NoTabCharacter
        ParameterReassignment
        PublicMethodsBeforeNonPublicMethods
        StaticFieldsBeforeInstanceFields
        StaticMethodsBeforeInstanceMethods
        TernaryCouldBeElvis
        TrailingComma
        VariableTypeRequired
        VectorIsObsolete

        // rulesets/design.xml
        AbstractClassWithPublicConstructor
        AbstractClassWithoutAbstractMethod
        AssignmentToStaticFieldFromInstanceMethod
        BooleanMethodReturnsNull
        //BuilderMethodWithSideEffects
        CloneableWithoutClone
        CloseWithoutCloseable
        CompareToWithoutComparable
        ConstantsOnlyInterface
        EmptyMethodInAbstractClass
        FinalClassWithProtectedMember
        Instanceof
        LocaleSetDefault
        NestedForLoop
        PrivateFieldCouldBeFinal
        PublicInstanceField
        ReturnsNullInsteadOfEmptyArray
        ReturnsNullInsteadOfEmptyCollection
        SimpleDateFormatMissingLocale
        StatelessSingleton
        ToStringReturnsNull

        // rulesets/dry.xml
        DuplicateListLiteral
        DuplicateMapLiteral
        DuplicateNumberLiteral
        DuplicateStringLiteral

        
        // rulesets/exceptions.xml
        CatchArrayIndexOutOfBoundsException
        CatchError
        CatchException
        CatchIllegalMonitorStateException
        CatchIndexOutOfBoundsException
        CatchNullPointerException
        CatchRuntimeException
        CatchThrowable
        ConfusingClassNamedException
        ExceptionExtendsError
        ExceptionExtendsThrowable
        ExceptionNotThrown
        MissingNewInThrowStatement
        ReturnNullFromCatchBlock
        SwallowThreadDeath
        ThrowError
        ThrowException
        ThrowNullPointerException
        ThrowRuntimeException
        ThrowThrowable


        // rulesets/generic.xml
        IllegalClassMember
        IllegalClassReference
        IllegalPackageReference
        IllegalRegex
        IllegalString
        IllegalSubclass
        RequiredRegex
        RequiredString
        StatelessClass


        GrailsStatelessService

        // rulesets/groovyism.xml
        AssignCollectionSort
        AssignCollectionUnique
        ClosureAsLastMethodParameter
        CollectAllIsDeprecated
        ConfusingMultipleReturns
        ExplicitArrayListInstantiation
        ExplicitCallToAndMethod
        ExplicitCallToCompareToMethod
        ExplicitCallToDivMethod
        ExplicitCallToEqualsMethod
        ExplicitCallToGetAtMethod
        ExplicitCallToLeftShiftMethod
        ExplicitCallToMinusMethod
        ExplicitCallToModMethod
        ExplicitCallToMultiplyMethod
        ExplicitCallToOrMethod
        ExplicitCallToPlusMethod
        ExplicitCallToPowerMethod
        ExplicitCallToRightShiftMethod
        ExplicitCallToXorMethod
        GStringAsMapKey
        GStringExpressionWithinString
        GetterMethodCouldBeProperty
        GroovyLangImmutable
        UseCollectMany
        UseCollectNested

        // rulesets/imports.xml
        DuplicateImport
        ImportFromSamePackage
        ImportFromSunPackages
        MisorderedStaticImports
        NoWildcardImports
        UnnecessaryGroovyImport
        UnusedImport


        // rulesets/junit.xml
//        ChainedTest
//        CoupledTestCase
//        JUnitAssertAlwaysFails
//        JUnitAssertAlwaysSucceeds
//        JUnitFailWithoutMessage
//        JUnitLostTest
//        JUnitPublicField
//        JUnitPublicNonTestMethod
//        JUnitPublicProperty
//        JUnitSetUpCallsSuper
//        JUnitStyleAssertions
//        JUnitTearDownCallsSuper
//        JUnitTestMethodWithoutAssert
//        JUnitUnnecessarySetUp
//        JUnitUnnecessaryTearDown
//        JUnitUnnecessaryThrowsException
//        SpockIgnoreRestUsed
//        UnnecessaryFail
//        UseAssertEqualsInsteadOfAssertTrue
//        UseAssertFalseInsteadOfNegation
//        UseAssertNullInsteadOfAssertEquals
//        UseAssertSameInsteadOfAssertTrue
//        UseAssertTrueInsteadOfAssertEquals
//        UseAssertTrueInsteadOfNegation

        // rulesets/logging.xml
        LoggerForDifferentClass
        LoggerWithWrongModifiers
        LoggingSwallowsStacktrace
        MultipleLoggers
        PrintStackTrace
        Println
        SystemErrPrint
        SystemOutPrint

        // rulesets/naming.xml
        AbstractClassName
        ClassName
        ClassNameSameAsFilename
        ClassNameSameAsSuperclass
        ConfusingMethodName
        FactoryMethodName
        // FieldName: proposes fieldNames to be uppercase - don't want that!
        InterfaceName
        InterfaceNameSameAsSuperInterface
        MethodName
        ObjectOverrideMisspelledMethodName
        PackageName
        PackageNameMatchesFilePath
        ParameterName
        PropertyName
        VariableName

        // rulesets/security.xml
        FileCreateTempFile
        NonFinalPublicField
        NonFinalSubclassOfSensitiveInterface
        ObjectFinalize
        PublicFinalizeMethod
        SystemExit
        UnsafeArrayDeclaration

    
        // rulesets/size.xml
        ClassSize
        MethodCount
        MethodSize
        NestedBlockDepth
        ParameterCount
        

        // rulesets/unused.xml
        UnusedArray
        UnusedMethodParameter
        UnusedObject
        UnusedPrivateField
        UnusedPrivateMethod
        UnusedPrivateMethodParameter
        UnusedVariable
}