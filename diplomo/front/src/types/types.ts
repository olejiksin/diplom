export interface User {
    login: string;
    password: string;
}


export interface MainActions {
    type: string;
    payload: any;
}

export interface MainState {
    loginForServer: string | null;
    passwordForServer: string | null;
    serverType: serverTypeForDeploy | null;
    appType: applicationType | null;
    rep: string;
    ip: string;
    port: string;
    project:string;
    subMicroPort:string;
    appName: string;
    appLang: string;
    workerList: Worker[];
    workerIp:string;
    workerPassword:string;
    workerLogin:string;
}
export interface MicroApp{
    name:string;
    id: string;
    link: string;
    status: AppServerStatus;
    appType: applicationType;
    micros?:SubMicroApp[];
}
export interface SubMicroApp{
    appLang:string;
    name:string;
    ip:string;
    status:AppServerStatus;
    port:string;
}
export interface MicroAppWithServerData extends MicroApp{
    // serverType: serverTypeForDeploy;
    ip: string;
    login: string;
    password: string;
    workerList:Worker[];
    projectPort: object;
}
export interface Worker {
    ip: string;
    login: string;
    password: string;
}

export interface LogRegAction {
    type: string;
    payload: string;
}

export interface LoginState {
    emailOrLogin: string;
    password: string;
}

export interface Server {
    name: string;
    status: AppServerStatus;
    ip: string;
}

export interface App extends SubMicroApp{
    id:string;
    link: string,
    port: string,
    appType: null | applicationType,
}

export interface Database {
    id: string;
    ip: string,
    status: AppServerStatus,
    dbType: dbType,
    port: string,
    name: string
}

export interface AppWithServerData extends App {
    serverType: null | serverTypeForDeploy;
    password: string;
    login: string;
}

export interface DataBaseWithServer extends Database {
    serverType: null | serverTypeForDeploy,
    user: string,
    pas: string,
}

export interface AppState {
    deployStatus:boolean;
    currentUser: string;
    error: string;
    profile: {
        appList: App[],
        databases: Database[],
        microservices:MicroApp[]
    };
}


export type dbType = 'postgresql' | 'mongodb' | 'mysql' | 'redis';
export type serverTypeForDeploy = 'own' | 'company';
export type applicationType = 'mono' | 'micro';
export type AppServerStatus = 'offline' | 'online';
