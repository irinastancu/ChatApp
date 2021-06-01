package util;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Format {

    public static TextFlow checkMessage(Text message){
        List<Pattern> patterns = new ArrayList<Pattern>();

        patterns.add(Pattern.compile("\\ \\*.+\\*\\ ",Pattern.CASE_INSENSITIVE)); //BOLD
        patterns.add(Pattern.compile("\\ \\_.+\\_\\ ",Pattern.CASE_INSENSITIVE)); //ITALIC
        patterns.add(Pattern.compile("\\ \\'.+\\'\\ ",Pattern.CASE_INSENSITIVE)); //UNDERLINE
        patterns.add(Pattern.compile("\\ \\~.+\\~\\ ",Pattern.CASE_INSENSITIVE)); //STRIKETHROUGH

        TextFlow flow = new TextFlow(message);

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher((CharSequence) ((Text) flow.getChildren().get(getLastIndexOfTextFlow(flow))).getText());
            if(matcher.find()) {
                flow = formatMessage(flow, matcher, pattern);
            }
        }

        return flow;
    }

    public static TextFlow formatMessage(TextFlow message, Matcher matcher, Pattern pattern){
        Text text = (Text)message.getChildren().get(getLastIndexOfTextFlow(message));

        String formatString = text.getText();
        Text frontText = new Text(formatString.substring(0,matcher.start()+1));
        Text middleText = new Text(formatString.substring(matcher.start()+2,matcher.end()-2));
        Text endText = new Text(formatString.substring(matcher.end()-1,formatString.length()));

        switch (pattern.pattern()) {
            case "\\ \\*.+\\*\\ ":
                middleText.setStyle("-fx-font-weight: bold");
                break;
            case "\\ \\_.+\\_\\ " :
                middleText.setFont(Font.font("Segue UI", FontPosture.ITALIC, 12));
                break;
            case "\\ \\'.+\\'\\ " :
                middleText.setUnderline(true);
                break;
            case "\\ \\~.+\\~\\ " :
                middleText.setStrikethrough(true);
                break;
        }
        message.getChildren().remove(getLastIndexOfTextFlow(message));
        message.getChildren().addAll(frontText,middleText,endText);

        return message;
    }

    public static int getLastIndexOfTextFlow(TextFlow text) {
        int counter = 0;

        for(javafx.scene.Node node : text.getChildren()) {
            counter++;
        }
        return counter - 1;
    }
}
