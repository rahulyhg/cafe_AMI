package co.geeksters.cafe_ami.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.cafe_ami.events.failure.ConnectionFailureEvent;
import co.geeksters.cafe_ami.events.failure.NoTodosFailureEvent;
import co.geeksters.cafe_ami.events.failure.UnauthorizedFailureEvent;
import co.geeksters.cafe_ami.events.success.CreateTodoEvent;
import co.geeksters.cafe_ami.events.success.DeleteTodosEvent;
import co.geeksters.cafe_ami.events.success.TodosEvent;
import co.geeksters.cafe_ami.events.success.UpdateTodoEvent;
import co.geeksters.cafe_ami.global.BaseApplication;
import co.geeksters.cafe_ami.interfaces.TodoInterface;
import co.geeksters.cafe_ami.models.Todo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TodoService {

    public final TodoInterface api;
    String token;

    public TodoService(String token) {
        this.api = BaseService.adapterWithToken(token).create(TodoInterface.class);
        this.token = token;
    }

    public void listTodosForMember() {
        this.api.listTodosForMember(new Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, Response rawResponse) {
                List<Todo> todos_for_member = Todo.createListTodosFromJson(response.getAsJsonObject().get("data").getAsJsonArray());
                BaseApplication.post(new TodosEvent(todos_for_member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                    if (error.getResponse().getStatus() == 404) {
                        BaseApplication.post(new NoTodosFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void createTodo(Todo todo) {
    //public void createTodo(int user_id, String text, List<User> associated_members, Integer remind_me_at) {

        this.api.createTodo(todo.text, Todo.arrayToString(todo.members), todo.remindMeAt, token, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Todo created_todo = Todo.createTodoFromJson(response);
                BaseApplication.post(new CreateTodoEvent(created_todo));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if (error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else if (error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                } else if (error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                } else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateTodo(Todo todo) {

        this.api.updateTodo(todo.id, todo.text, Todo.arrayToString(todo.members), todo.remindMeAt, token, "put", new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Todo updated_todo = Todo.createTodoFromJson(response);
                BaseApplication.post(new UpdateTodoEvent(updated_todo));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if (error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else if (error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                } else if (error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                } else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }
    public void updateTodoforDetach(Todo todo) {

        this.api.updateTodo(todo.id,todo.text , Todo.arrayToString(todo.members),todo.remindMeAt,token,"put", new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Todo updated_todo = Todo.createTodoFromJson(response);
                BaseApplication.post(new DeleteTodosEvent(new ArrayList<Todo>()));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void deleteTodo(int todo_id) {

        this.api.deleteTodo(todo_id, "delete", this.token, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Todo> todos = Todo.createListTodosFromJson(responseAsArray);
                BaseApplication.post(new DeleteTodosEvent(todos));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }
}
