import {
    ADD_NEW_APP,
    ADD_NEW_DB, ADD_NEW_MICRO,
    AppActions, CHANGE_DEPLOY_STATUS,
    DELETE_DB,
    EDIT_APP_SETTINGS,
    ERROR,
    EXIT,
    LOGIN,
    MAIN,
    REMOVE_APP,
    START_OR_STOP_APP,
    START_STOP_DB
} from "../types/actionTypes";
import {AppState} from "../types/types";

export const storage = localStorage;

export const initialState: AppState = {
    currentUser: storage.username || 'default',
    deployStatus: true,
    error: '',
    profile: {
        databases: [],
        appList: [],
        microservices: []
    }
};

export function appReducer(state = initialState, action: AppActions) {

    switch (action.type) {
        case MAIN: {
            return {
                ...state,
                profile: {
                    appList: action.apps,
                    databases: action.databases,
                    microservices: action.microservices
                }
            }
        }
        case ADD_NEW_APP: {
            let app = {
                name: action.app.name,
                port: action.app.port,
                ip: action.app.ip,
                id: action.app.id,
                status: action.app.status,
                link: action.app.link,
                type: action.app.appType,
                appLang: action.app.appLang
            };
            return {...state, profile: {appList: [...state.profile.appList, app]}, deployStatus: true}
        }
        case ADD_NEW_MICRO: {
            return {...state, profile: {microservices: [...state.profile.microservices, action.app]}}
        }
        case START_OR_STOP_APP: {
            let changed = state.profile.appList;
            for (let app of changed) {
                if (action.id === app.id) {
                    app.status = app.status === 'online' ? 'offline' : 'online';
                }
            }
            return {...state, profile: {appList: changed}}
        }
        case START_STOP_DB: {
            let changed = state.profile.databases;
            for (let db of changed) {
                if (action.id === db.id) {
                    db.status = db.status === 'online' ? 'offline' : "online";
                }
            }
            return {...state, profile: {databases: changed}}
        }
        case EXIT: {
            return {currentUser: '', error: '', profile: {appList: [], serversList: []}}
        }
        case LOGIN: {
            storage.username = action.user.login;
            return {...state, currentUser: action.user.login}
        }
        case ADD_NEW_DB: {
            let db = {
                id: action.dataBase.id,
                ip: action.dataBase.ip,
                status: action.dataBase.status,
                dbType: action.dataBase.dbType,
                port: action.dataBase.port
            }
            return {...state, profile: {database: [...state.profile.databases, db]}}
        }
        case CHANGE_DEPLOY_STATUS: {
            return {...state, deployStatus: false}
        }
        case EDIT_APP_SETTINGS: {
            let changed = state.profile.appList;
            for (let chApp of changed) {
                if (action.app.id === chApp.id) {
                    chApp = action.app
                }
            }
            return {...state, profile: {appList: changed}}
        }
        case REMOVE_APP: {
            let changed = state.profile.appList.filter(({id}) => id !== action.id);
            return {...state, profile: {appList: changed}}
        }
        case DELETE_DB: {
            let changed = state.profile.databases.filter(({id}) => id !== action.id);
            return {...state, profile: {database: changed}}
        }
        case ERROR: {
            return {...state, error: action.er, deployStatus: true}
        }
        default: {
            return {...state};
        }
    }
}
