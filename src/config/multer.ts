import multer from 'multer'
import path from 'path'
import crypto from 'crypto'

const storage = multer.diskStorage({
  destination: './public/',
  filename: function(req, file, cb) {
    return crypto.randomBytes(16, function(err, raw) {
      if (err) {
        return cb(err, '')
      }
      return cb(null, '' + (raw.toString('hex')) + (path.extname(file.originalname)))
    })
  }
})

const upload = multer({
  storage: storage,
  fileFilter: (req, file, cb) => {
    const ext = path.extname(file.originalname)
    if (ext !== '.png') {
      return cb(new Error('File must be .png'))
    }
    return cb(null, true)
  },
  limits: {
    fileSize: 1024 * 300
  }
})

export { upload }
