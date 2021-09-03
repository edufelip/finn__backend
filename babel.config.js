module.exports = {
  presets: [
    [
      '@babel/preset-env',
      {
        targets: {
          node: 'current'
        }
      }
    ],
    '@babel/preset-typescript'
  ],
  plugins: [
    ['module-resolver', {
      alias: {
        '@models': './src/models',
        '@interfaces': './src/interfaces',
        '@data': './src/data',
        '@service': './src/service'
      }
    }]
  ],
  ignore: [
    '**/*.spec.ts'
  ]
}
