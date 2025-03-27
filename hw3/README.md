# Ben Juntilla's HW2

## UML Diagrams

### Question Class

```mermaid
classDiagram
    class Question {
        -String questionId
        -String title
        -String description
        -List~String~ tags
        -List~Answer~ answers
        -String resolvedBy
        -String authorUsername
        +Question(String title, String description, List~String~ tags, String authorUsername)
        +String getQuestionId()
        +String getTitle()
        +String getDescription()
        +List~String~ getTags()
        +List~Answer~ getAnswers()
        +boolean getResolvedBy()
        +String getAuthorUsername()
        +void setTitle(String title)
        +void setDescription(String description)
        +void setTags(List~String~ tags)
        +void setAuthorUsername(String authorUsername)
        +void addAnswer(Answer answer)
        +void setResolvedBy(boolean resolvedBy)
    }
```

### Answer Class

```mermaid
classDiagram
    class Answer {
        -String questionId
        -String answerId
        -String content
        -String author
        -Date timestamp
        +Answer(String content, String author)
        +String getQuestionId()
        +String getAnswerId()
        +String getContent()
        +String getAuthor()
        +Date getTimestamp()
        +void setContent(String content)
        +void setQuestionId(String questionId)
        +void setAnswerId(String answerId)
    }
```

### Questions List Class

```mermaid
classDiagram
    class QuestionsList {
        -List~Question~ questions
        +QuestionsList()
        +void addQuestion(Question question)
        +void removeQuestion(String questionId)
        +Question getQuestion(String questionId)
        +List~Question~ searchQuestions(String query)
    }
```

### Answers List Class

```mermaid
classDiagram
    class AnswersList {
        -List~Answer~ answers
        +AnswersList()
        +void addAnswer(Answer answer)
        +void removeAnswer(String answerId)
        +Answer getAnswer(String answerId)
        +List~Answer~ searchAnswers(String query)
    }
```

### Sequence Diagram for Creating a Question

```mermaid
sequenceDiagram
    participant Student
    participant QuestionsList
    participant Question

    Student->>QuestionsList: addQuestion(title, description, tags)
    QuestionsList->>Question: new Question(title, description, tags)
    QuestionsList-->>Student: Question created successfully
```

### Sequence Diagram for Reading a Question

```mermaid
sequenceDiagram
    participant Student
    participant QuestionsList
    participant Question

    Student->>QuestionsList: getQuestion(questionId)
    QuestionsList->>Question: getQuestionId()
    Question-->>QuestionsList: Question details
    QuestionsList-->>Student: Return question details
```

### Sequence Diagram for Updating a Question

```mermaid
sequenceDiagram
    participant Student
    participant QuestionsList
    participant Question

    Student->>QuestionsList: getQuestion(questionId)
    QuestionsList->>Question: getQuestionId()
    Question-->>QuestionsList: Question details
    QuestionsList-->>Student: Return question details
    Student->>Question: setTitle(newTitle)
    Student->>Question: setDescription(newDescription)
    Question-->>Student: Question updated successfully
```

### Sequence Diagram for Deleting a Question

```mermaid
sequenceDiagram
    participant Student
    participant QuestionsList
    participant Question

    Student->>QuestionsList: removeQuestion(questionId)
    QuestionsList->>Question: getQuestionId()
    QuestionsList-->>Student: Question deleted successfully
```

### Sequence Diagram for Creating an Answer

```mermaid
sequenceDiagram
    participant Student
    participant AnswersList
    participant Answer

    Student->>AnswersList: addAnswer(content, author)
    AnswersList->>Answer: new Answer(content, author)
    AnswersList-->>Student: Answer created successfully
```

### Sequence Diagram for Reading an Answer

```mermaid
sequenceDiagram
    participant Student
    participant AnswersList
    participant Answer

    Student->>AnswersList: getAnswer(answerId)
    AnswersList->>Answer: getAnswerId()
    Answer-->>AnswersList: Answer details
    AnswersList-->>Student: Return answer details
```

### Sequence Diagram for Updating an Answer

```mermaid
sequenceDiagram
    participant Student
    participant AnswersList
    participant Answer

    Student->>AnswersList: getAnswer(answerId)
    AnswersList->>Answer: getAnswerId()
    Answer-->>AnswersList: Answer details
    AnswersList-->>Student: Return answer details
    Student->>Answer: setContent(newContent)
    Answer-->>Student: Answer updated successfully
```

### Sequence Diagram for Deleting an Answer

```mermaid
sequenceDiagram
    participant Student
    participant AnswersList
    participant Answer

    Student->>AnswersList: removeAnswer(answerId)
    AnswersList->>Answer: getAnswerId()
    AnswersList-->>Student: Answer deleted successfully
```
