# AndroidQuizApp
This is a fully customizable quiz app made with a lot of effort and focused on learning.
This project is part of Udacity - Google Developer Challenge Scholarship : Android Basics and makes use of different technics to bring edition comfort. Amazingly all the files that you need to edit to have a completelly different scenario is questions.json and arrays.xml files. 
 This app also uses sound effects from "https://www.zapsplat.com“, thank's to zapsplat for this amazing sound effects.



<h2>Application show room</h2>

<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/app/horizontal/ShowRoom.png" alt="Android quiz app show room image" />
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/app/horizontal/ShowRoom2.png" alt="Android quiz app show room image" />

See more images on <a href="https://github.com/FabioGouveia/AndroidQuizApp/wiki" title="Go to android quiz app wiki">Android quiz app wiki</a>

**Edit Instructions:**


<ul>
    <li><a href="#add_edit_or_remove_levels" title="How to add, edit or remove a level">Add, edit or remove a level</a></li>
    <li><a href="#edit_level_strings" title="How can I add, edit or remove level strings">How can I add, edit or remove level strings</a></li>
    <li><a href="#setup_level_icons" title="How can I add, edit or remove level icons">How can I add, edit or remove level icons</a></li>
    <li><a href="#add_edit_or_remove_questions" title="How to add, edit or remove a question">How can I add, edit or remove a question</a></li>
    <li><a href="#setup_multiple_choice_question" title="How to add or edit a multiple choice question">How can I setup a multiple choice question</a></li>
    <li><a href="#setup_one_choice_question" title="How to add or edit one choice questions">How can I setup one choice questions</a></li>
    <li><a href="#setup_yes_or_no_question" title="How to add or edit yes/no questions">How can I add or edit yes/no questions</a></li>
    <li><a href="#setup_textual_question" title="How to add textual questions">How can I add textual questions</a></li>
    <li><a href="#setup_textual_question" title="How to add textual questions">How can I add, remove and edit sound effects</a></li>
</ul>

<h2>Adapt to your needs</h2>
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/ResourcesEditionPath.PNG" alt="Adaptable application image" title="Adaptable application" />

<h3 name="add_edit_or_remove_levels">Add, edit or remove levels</h3>
<p>Add, edit and remove levels in a simple way through a xml file called arrays.xml stored in values folder.</p>

<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/ArraysPath.PNG" alt="How to add, edit and remove levels image" title="How to add, edit and remove levels" />

<h3 name="edit_level_strings">Edit level strings</h3>

<p>Resources fast edition...<p>
<p>Add, remove or edit level names, scores, earnings and more just in one file, <b>arrays.xml</b> file</p>
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/FastResourcesEdition.PNG" alt="How to add, remove or edit level strings image" title="How to add, remove or edit level strings" />

<p name="setup_level_icons">Setup your level icons with enabled and disabled state...</p>
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/IconsFastEdition.PNG" alt="How to edit, add or remove level icons image" title="How to edit, add or remove level icons" />


<h3 name="edit_questions">Edit questions</h3>

<h4 name="add_edit_or_remove_questions">Add, edit or remove questions</h4>
<p>Add, edit and remove questions in a simple way through a json file called question.json that is stored in assets folder.</p>

<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/JSONQuestionsFilePath.PNG" alt="How to add and remove questions image" title="How to add, edit and remove questions" />

<p>JSON fast question edition...<p>
<p>Add a question and simple turn your questions in multiple choice, one choice and textual questions by editing boolean second parameter on <i>options</i> JSON array.</p>

<p name="setup_multiple_choice_question">Setup multiple answer questions by adding more <b>true</b> boolean values to options array.</p>
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/EditMultipleAnswerQuestions.PNG" alt="How to edit a multiple answer question image" title="How to edit a multiple answer question" />


<p name="setup_one_choice_question">Set up multiple answer/one choice question by adding just one <b>true</b> value to the right answer on the <i>options</i> array.</p>
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/OneChoiceMultipleAnswers.PNG" alt="How to edit a multiple answer, one choice question image" title="How to edit a multiple answer, one choice question" />


<p name="setup_yes_or_no_question">Add a <i>yes</i> or <i>no</i> question by adding just two elements to the <i>options</i> array.</p>
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/YesOrNoQuestion.PNG" alt="How to edit a yes or no question image" title="How to edit or add a yes or no question" />


<p name="setup_textual_question">You also can add textual questions by adding just one element to the <i>options</i> array, always setted to true.</p>
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/TextualQuestionEdition.PNG" alt="How to setup a textual question image" title="How to add a textual question" />
<p>One string for right answers as an array of strings separated by commas...</p>

<p name="setup_sound_effects">Setup your sound effects.</p>
<img src="https://github.com/FabioGouveia/AndroidQuizApp/blob/master/images/edition/SoundsPath.PNG" alt="How to edit, add or remove sound effects image" title="How to edit, add or remove sound effects" />
Sound effects from "https://www.zapsplat.com“