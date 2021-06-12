import {
    App,
    AppWithServerData,
    Database,
    DataBaseWithServer,
    MicroApp,
    MicroAppWithServerData,
    Server,
    User
} from "./types";

export const LOGIN = 'LOGIN';
export const ERROR = 'ERROR';
export const MAIN = 'MAIN';
export const ADD_NEW_SERVER = 'ADD_NEW_SERVER';
export const REMOVE_SERVER = 'REMOVE_SERVER';
export const EDIT_SERVER_SETTINGS = 'EDIT_SERVER_SETTINGS';
export const ADD_NEW_APP = 'ADD_NEW_APP';
export const REMOVE_APP = 'REMOVE_APP';
export const EDIT_APP_SETTINGS = 'EDIT_APP_SETTINGS';
export const CREATE_NEW_CONFIG = 'CREATE_NEW_CONFIG';
export const EXIT = 'EXIT';
export const START_OR_STOP_APP = 'START_OR_STOP_APP';
export const ADD_NEW_DB = 'ADD_NEW_DB';
export const DELETE_DB = 'DELETE_DB';
export const START_STOP_DB = 'START_STOP_DB';
export const ADD_NEW_MICRO = ' ADD_NEW_MICRO';
export const CHANGE_DEPLOY_STATUS = 'CHANGE_DEPLOY_STATUS';

export interface changeDeployStatus {
    type: typeof CHANGE_DEPLOY_STATUS;
}

export interface addNewMicroApp {
    type: typeof ADD_NEW_MICRO;
    app: MicroApp;
}

export interface addNewDb {
    type: typeof ADD_NEW_DB;
    dataBase: DataBaseWithServer;
}

export interface StartStopApp {
    type: typeof START_OR_STOP_APP;
    id: string;
}

export interface Exit {
    type: typeof EXIT;
}

export interface CreateConfig {
    type: typeof CREATE_NEW_CONFIG;
    configData: string;
}

export interface Login {
    type: typeof LOGIN;
    user: User;
}

export interface Error {
    type: typeof ERROR;
    er: string;
}

export interface Main {
    type: typeof MAIN;
    apps: App[];
    databases: Database[];
    microservices:MicroApp[];
}

export interface AddNewServer {
    type: typeof ADD_NEW_SERVER;
    server: Server;
}

export interface AddNewApp {
    type: typeof ADD_NEW_APP;
    app: AppWithServerData;
}

export interface RemoveServer {
    type: typeof REMOVE_SERVER;
    name: string;
}

export interface RemoveDb {
    type: typeof DELETE_DB;
    id: string;
}

export interface RemoveApp {
    type: typeof REMOVE_APP;
    id: string;
}

export interface EditServer {
    type: typeof EDIT_SERVER_SETTINGS;
    server: Server;
}

export interface EditApp {
    type: typeof EDIT_APP_SETTINGS;
    app: App;
}

export interface StartStopDb {
    type: typeof START_STOP_DB;
    id: string;
}

export type ServerAppActionTypes =
    AddNewServer
    | AddNewApp
    | RemoveServer
    | RemoveApp
    | EditServer
    | EditApp
    | CreateConfig
    | StartStopApp
    | RemoveDb
    | StartStopDb
    | addNewDb
    |changeDeployStatus
    |addNewMicroApp;

export type MainActionTypes = Login | Error | Main | Exit;

export type AppActions = MainActionTypes | ServerAppActionTypes;
