import axios from 'axios';
import {
    ADD_NEW_APP, ADD_NEW_DB, ADD_NEW_MICRO, ADD_NEW_SERVER,
    AppActions, CHANGE_DEPLOY_STATUS, CREATE_NEW_CONFIG, DELETE_DB,
    EDIT_APP_SETTINGS,
    EDIT_SERVER_SETTINGS,
    ERROR, EXIT,
    LOGIN,
    MAIN,
    REMOVE_APP,
    REMOVE_SERVER, START_OR_STOP_APP, START_STOP_DB
} from "../types/actionTypes";
import {Dispatch} from "redux";
import {
    App,
    AppWithServerData,
    Database,
    DataBaseWithServer, MicroApp,
    MicroAppWithServerData,
    Server,
    User
} from '../types/types';

export const addNewMicroApp = (app: MicroAppWithServerData, login: string) => {
    return (dispatch: Dispatch<AppActions>) => {
        axios.post('/newMicro', app, {headers: {'login': login}})
            .then((r) => {
                dispatch(addNewMicro(r.data));
            })
            .catch((er) => {
                alert(er.response.data);
                dispatch(Error(er));
            })
    }
};
export const addNewDB = (file: any, db: DataBaseWithServer, login: string) => {
    return (dispatch: Dispatch<AppActions>) => {
        let data = new FormData();
        data.append('file', file);
        data.append('db', JSON.stringify(db));
        axios.post('/newDb', data, {headers: {'login': login, 'Content-Type': 'multipart/form-data'}})
            .then((r) => {
                db.id = r.data;
                dispatch(newDb(db));
            })
            .catch((e) => {
                dispatch(Error(e));
            })
    };
}

export const startOrStopApp = (id: string) => {
    return (dispatch: Dispatch<AppActions>) => {
        axios.put(`/startOrStop/${id}`)
            .then(() => {
                dispatch(StartOrStopApp(id));
            })
            .catch((er) => {
                dispatch(Error(er))
            });
    }
};

export const exit = (props: any) => {
    window.localStorage.clear();
    return (dispatch: Dispatch<AppActions>) => {
        dispatch(DoExit());
        props.history.push('/login');
    };
};


export const login = (user: User, props: any) => {
    return (dispatch: Dispatch<AppActions>) => {
        axios.post('/login', user)
            .then(() => {
                dispatch(LogIn(user));
                props.history.push(`/main/${user.login}`);
            })
            .catch((er) => {
                dispatch(Error(er));
                alert('Wrong login or password');
            });
    };
};

export const getMainData = (login: string) => {
    return (dispatch: Dispatch<AppActions>) => {
        axios.get(`/main/${login}`)
            .then((resp) => {
                dispatch(Main(resp.data.apps, resp.data.dataBases, resp.data.microservices))
            })
            .catch((er) => dispatch(Error(er)));
    };
};

export const addNewServer = (server: Server, login: string) => {
    return (dispatch: Dispatch<AppActions>) => {
        axios.post('/newServer', server, {headers: {user: login}})
            .then((r) => {
                dispatch(AddNewServer(server));
                console.log(r)
            })
            .catch((er) => {
                console.log(er);
                dispatch(Error(er))
            });
    }
};

export const addNewApp = (app: AppWithServerData, login: string) => {
    return (dispatch: Dispatch<AppActions>) => {
        dispatch(changeDeployStatus());
        axios.post('/newApp', app, {headers: {user: login}})
            .then((r) => {
                app.id = r.data;
                if (app.serverType === "company") {
                    app.ip = '169.254.202.98';
                }
                dispatch(AddNewApp(app))
            })
            .catch((er) => {
                console.log(er.response)
                alert(er);
                dispatch(Error(er))
            });
    }
};

export const removeServer = (name: string, login: string) => {
    return (dispatch: Dispatch<AppActions>) => {
        axios.delete(`/deleteServer/${name}`, {headers: {user: login}})
            .then(() => dispatch(RemoveServer(name)))
            .catch((er) => dispatch(Error(er)));
    }
};

export const removeApp = (id: string) => {
    return (dispatch: Dispatch<AppActions>) => {
        axios.delete(`/deleteApp/${id}`)
            .then(() => dispatch(RemoveApp(id)))
            .catch((er) => dispatch(Error(er)));
    }
};
export const startOrStopDB = (id: string) => {
    return (dispatch: Dispatch<AppActions>) => {
        axios.post(`/startOrStopDB/${id}`)
            .then((r) => {
                dispatch(StartStopDB(id));
            })
            .catch((er) => {
                dispatch(Error(er));
            })
    }
}
export const removeDB = (id: string) => {
    return (dispatch: Dispatch<AppActions>) => {
        axios.delete(`/deleteDB/${id}`)
            .then(() => {
                dispatch(DeleteDb(id));
            })
            .catch((e) => {
                dispatch(Error(id));
            })
    }
}
export const editServer = (server: Server, login: string) => {
    return (dispatch: Dispatch<AppActions>) => {
        axios.put('/editServer', server, {headers: {user: login}})
            .then(() => dispatch(EditServer(server)))
            .catch((er) => dispatch(Error(er)));
    }
};
export const editApp = (app: App) => {
    return (dispatch: Dispatch<AppActions>) => {
        axios.put('/editApp', app)
            .then(() => dispatch(EditApp(app)))
            .catch((er) => dispatch(Error(er)));
    }
};
export const changeDeployStatus = (): AppActions => {
    return {
        type: CHANGE_DEPLOY_STATUS
    }
}
export const addNewMicro = (app: MicroApp): AppActions => {
    return {
        type: ADD_NEW_MICRO,
        app
    }
}

export const StartStopDB = (id: string): AppActions => {
    return {
        type: START_STOP_DB,
        id
    }
}
export const DeleteDb = (id: string): AppActions => {
    return {
        type: DELETE_DB,
        id
    }
}
export const StartOrStopApp = (id: string): AppActions => {
    return {
        type: START_OR_STOP_APP,
        id
    }
};
export const CreateConfig = (configData: string): AppActions => {
    return {
        type: CREATE_NEW_CONFIG,
        configData
    }
};
export const AddNewServer = (server: Server): AppActions => {
    return {
        type: ADD_NEW_SERVER,
        server
    }
};
export const AddNewApp = (app: AppWithServerData): AppActions => {
    return {
        type: ADD_NEW_APP,
        app
    }
};
export const EditApp = (app: App): AppActions => {
    return {
        type: EDIT_APP_SETTINGS,
        app
    }
};
export const EditServer = (server: Server): AppActions => {
    return {
        type: EDIT_SERVER_SETTINGS,
        server
    }
};
export const RemoveServer = (name: string): AppActions => {
    return {
        type: REMOVE_SERVER,
        name
    }
};
export const RemoveApp = (id: string): AppActions => {
    return {
        type: REMOVE_APP,
        id
    }
};
export const Main = (apps: App[], databases: Database[], microservices: MicroApp[]): AppActions => {
    return {
        type: MAIN,
        apps, databases, microservices
    }
};
export const LogIn = (user: User): AppActions => {
    return {
        type: LOGIN,
        user
    }
};
export const DoExit = (): AppActions => {
    return {
        type: EXIT
    }
};
export const Error = (er: string): AppActions => {
    return {
        type: ERROR,
        er
    }
};
export const newDb = (dataBase: DataBaseWithServer): AppActions => {
    return {
        type: ADD_NEW_DB,
        dataBase
    }
}
