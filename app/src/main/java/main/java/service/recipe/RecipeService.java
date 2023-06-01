package main.java.service.recipe;

import android.util.Log;

import main.java.BackgroundThread;
import main.java.model.SearchResult;
import main.java.service.history.HistoryService;
import main.java.util.http.HttpService;
import main.java.util.parser.ResultParser;

public abstract class RecipeService {

    protected final HttpService httpService;
    protected final ResultParser resultParser;
    protected final HistoryService historyService;

    public RecipeService(HttpService httpService, ResultParser resultParser, HistoryService historyService) {
        this.httpService = httpService;
        this.resultParser = resultParser;
        this.historyService = historyService;
    }

    // private, public, protected

    public SearchResult search(String word) {
        String response;

        try {
            // http 통신을 통해 response 확인
            BackgroundThread gptBack = new BackgroundThread(word + " 레시피");
            Thread gptThread = new Thread(gptBack);

            gptThread.start();

            try {
                gptThread.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            response = gptBack.getResponse();

            Log.d("TAG", "search: 통신 성공!" + response);
        } catch (Exception e) {
            // 애러 로직
            Log.d("TAG", "통신 실패!");
            e.printStackTrace();
            return null;
        }

        // response 를 파싱하여 searchResult 에 저장
        SearchResult searchResult = resultParser.getSearchResultByResponse(response);

        searchResult.setRecipeName(word);
        Log.d("TAG", "레시피명 입력 성공!" + word);
        // 검색 결과룰 history 에 추가
        addHistory(searchResult);

        return searchResult;
    }

    protected abstract void addHistory(SearchResult searchResult);
}
