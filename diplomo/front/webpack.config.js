// const path = require('path');
// const HtmlWebPackPlugin = require("html-webpack-plugin");
// // const {CleanWebpackPlugin} = require('clean-webpack-plugin');
//
// module.exports = {
//     entry: "./src/index.tsx",
//     mode: "development",
//     output: {
//         publicPath: "/",
//         filename: "./main.js"
//     },
//     devServer: {
//         contentBase: path.join(__dirname, "dist"),
//         compress: true,
//         port: 3000,
//         watchContentBase: true,
//         progress: true,
//         historyApiFallback: true
//     },
//     module: {
//         rules: [
//             {
//                 test: /\.m?js$/,
//                 exclude: /(node_modules|bower_components)/,
//                 use: {
//                     loader: "babel-loader"
//                 }
//             },
//             {
//                 test: /\.tsx?$/,
//                 use: 'ts-loader',
//                 exclude: /node_modules/,
//             },
//             {
//                 test: /\.s[ac]ss$/i,
//                 use: [
//                     'style-loader',
//                     'css-loader',
//                     'sass-loader',
//                 ],
//             },
//             {
//                 test: /\.css$/,
//                 use: [
//                     "style-loader",
//                     {
//                         loader: "css-loader",
//                         options: {
//                             modules: true
//                         }
//                     }
//                 ]
//             },
//             {
//                 test: /\.(png|svg|jpg|gif)$/,
//                 use: ["file-loader"]
//             }
//         ]
//     },
//     resolve: {
//         extensions: ['.tsx', '.ts', '.js', '.jsx'],
//     },
//     plugins: [
//         new HtmlWebPackPlugin({
//             template: path.resolve(__dirname, 'dist/index.html'),
//             filename: 'index.html'
//         })
//     ]
// };
//
