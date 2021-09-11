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

export default storage
