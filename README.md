# AndroidQuizApp
This is a fully customizable quiz app made with a lot of effort and focused on learning.
This project is part of Udacity - Google Developer Challenge Scholarship : Android Basics and makes use of different technics to bring edition comfort. Amazingly all the files that you need to edit to have a completelly different scenario is questions.json and arrays.xml files. 

**Edit Instructions:**


<ul>
    <li><a href="#add_edit_or_remove_levels" title="How to add, edit or remove a level">Add, edit or remove a level</a></li>
    <li><a href="#add_edit_or_remove_questions" title="How to add, edit or remove a question">Add, edit or remove questions</a></li>
    <li><a href="#setup_multiple_choice_question" title="How to add or edit a multiple choice question">How can I setup a multiple choice question</a></li>
    <li><a href="#setup_one_choice_question" title="How to add or edit one choice questions">How can I setup one choice questions</a></li>
    <li><a href="#setup_yes_or_no_question" title="How to add or edit yes/no questions">How can I add or edit yes/no questions</a></li>
    <li><a href="#setup_textual_question" title="How to add textual questions">How can I add textual questions</a></li>
</ul>

<h3 name="add_edit_or_remove_levels">Add, edit or remove levels</h3>
<p>Add, edit and remove levels in a simple way through a xml file called arrays.xml that is stored in values folder.</p>

<h3 name="add_edit_or_remove_questions">Add, edit or remove questions</h3>
<p>Add, edit and remove questions in a simple way through a json file called question.json that is stored in assets folder.</p>

<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/JSONQuestionsFilePath.PNG" alt="How to add and remove questions image" title="How to add and remove questions" />


<h4 name="edit_questions">Edit questions</h4>

<p>JSON fast question edition...<p>
<p>Add a question and simple turn your questions in multiple choice, one choice and textual questions by editing boolean second parameter on <i>options</i> JSON array.</p>

<p name="setup_multiple_choice_question">Set up multiple answers questions by adding more <b>true</b> values to options array.</p>
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/EditMultipleAnswerQuestions.PNG" alt="How to edit a multiple answer question image" title="How to edit a multiple answer question" />


<p name="setup_one_choice_question">Set up multiple answer, one choice question by adding just one <b>true</b> value to the right answer on the <i>options</i> array.</p>
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/OneChoiceMultipleAnswers.PNG" alt="How to edit a multiple answer, one choice question image" title="How to edit a multiple answer, one choice question" />


<p name="setup_yes_or_no_question">Add a <i>yes</i> or <i>no</i> question by adding just two to elements to the <i>options</i> array.</p>
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/YesOrNoQuestion.PNG" alt="How to edit a yes or no question image" title="How to edit or add a yes or no question" />


<p name="setup_textual_question">You also can add textual questions by adding just one element to the <i>options</i> array, always setted to true.</p>
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/TextualQuestionEdition.PNG" alt="How to setup a textual question image" title="How to add a textual question" />
<p>Add the possible answer as an array of strings separated by commas...</p>

This app uses nice sound effects from "https://www.zapsplat.com“, thank's to zapsplat for this amazing
sound effects.