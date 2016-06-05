 #!/bin/bash
export LC_CTYPE=C 
export LANG=C

FNAME="default_answers.json"
LEN=$(cat $FNAME | jsawk 'return this.data.length')
echo "Answers - $LEN"
> strings.xml

    
printf '<string name="answers_count">%d</string>\n\n' $LEN >> strings.xml
for i in `seq 0 $(($LEN-1))`
do
    ANSW=$(cat $FNAME | jsawk "return this.data[$i].text")
    AUTH=$(cat $FNAME | jsawk "return this.data[$i].author")
    
    ANSW=${ANSW//\"/POPO}
    AUTH=${AUTH//\"/POPO}
    
    printf '<string name="answer_%d">%s</string>\n<string name="author_%d">%s</string>\n' $i "$ANSW" $i "$AUTH" | iconv -f ascii -t utf8//IGNORE >> strings.xml
    PROGRESS=$(echo "scale=2; $i*100/$LEN" | bc)
    echo -ne " Progress: $PROGRESS\r"
    
done